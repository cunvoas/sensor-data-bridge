package com.github.cunvoas.clientcollector.client;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;
import com.github.cunvoas.clientcollector.model.TTNCollectedMessage;
import java.net.http.HttpRequest.BodyPublishers;

/**
 * Client HTTP basique pour sauver les mesures TTN.
 */
public class TTNCollectorClient {
	private static final Logger LOG = Logger.getLogger(TTNCollectorClient.class.getName());
	private String collectorUrl;

	/**
	 * Paramétrage du service collecteur.
	 */
	public TTNCollectorClient() {
		Properties props = new Properties();
		try (InputStream input = getConfigInputStream()) {
			if (input == null) {
				LOG.log(Level.WARNING, "config.properties introuvable (ni en classpath, ni via la propriété JVM). Le client TTNCollector sera désactivé.");
				collectorUrl = null;
				return;
			}
			props.load(input);
			collectorUrl = props.getProperty("rust-collector.url");
			if (collectorUrl == null) {
				LOG.log(Level.WARNING, "La clé 'rust-collector.url' est absente de config.properties. Le client TTNCollector sera désactivé.");
				collectorUrl = null;
				return;
			}
		} catch (IOException e) {
			LOG.log(Level.WARNING, "Erreur lors du chargement de config.properties. Le client TTNCollector sera désactivé.", e);
			collectorUrl = null;
		}
	}

	/**
	 * Open an InputStream to the configuration file.
	 * Priority:
	 *  - JVM property "config" (e.g. -Dconfig=/path/to/config.properties)
	 *  - JVM property "config.file"
	 *  - JVM property "config.properties"
	 *  - classpath resource "config.properties"
	 */
	private InputStream getConfigInputStream() throws IOException {
		String path = System.getProperty("config");
		if (path == null || path.isBlank()) {
			path = System.getProperty("config.file");
		}
		if (path == null || path.isBlank()) {
			path = System.getProperty("config.properties");
		}
		if (path != null && !path.isBlank()) {
			Path p = Paths.get(path);
			return Files.newInputStream(p);
		}
		return getClass().getClassLoader().getResourceAsStream("config.properties");
	}
	
	/**
	 * Réalise un POST HTTP
	 * @param ttnMessage TTN content
	 * @return HTTP Code returned
	 */
	public Integer post(TTNCollectedMessage ttnMessage) {
		Integer http_code = 0;
		// if collector not configured, persist locally and return -1, but never throw
		if (collectorUrl == null || collectorUrl.isBlank()) {
			LOG.log(Level.INFO, "Collector URL not configured, persisting message locally");
			persistMessage(ttnMessage);
			return -1;
		}

		try {
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(ttnMessage);
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(collectorUrl))
				.header("Content-Type", "application/json")
				.POST(BodyPublishers.ofString(json))
				.build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			http_code = response.statusCode();
			return http_code;
		} catch (Exception e) {
			LOG.log(Level.WARNING, "Erreur lors de l'appel POST au collecteur, message persisté localement", e);
			persistMessage(ttnMessage);
			return -1;
		}
	}

	private void persistMessage(TTNCollectedMessage ttnMessage) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(ttnMessage);
			String dirName = "failed-ttn";
			Path dir = Paths.get(dirName);
			if (!Files.exists(dir)) {
				Files.createDirectories(dir);
			}
			String safeApp = sanitize(ttnMessage.getAppId());
			String safeDev = sanitize(ttnMessage.getDevId());
			String fileName = String.format("ttn_%d_%s_%s_%d.json", System.currentTimeMillis(), safeApp,
					safeDev, ttnMessage.getPort());
			Path out = dir.resolve(fileName);
			// write the compact JSON string as UTF-8 (single line)
			Files.write(out, json.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE_NEW);
		} catch (Exception ex) {
			LOG.log(Level.SEVERE, "Impossible de persister localement le message TTN", ex);
		}
	}

	private static String sanitize(String s) {
		if (s == null) {
			return "null";
		}
		return s.replaceAll("[^A-Za-z0-9_.-]", "_");
	}

	public String getCollectorUrl() {
		return collectorUrl;
	}

}