package com.github.cunvoas.clientcollector.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cunvoas.clientcollector.model.TTNCollectedMessage;
import java.net.http.HttpRequest.BodyPublishers;

/**
 * Client HTTP basique pour sauver les mesures TTN.
 */
public class TTNCollectorClient {
	private String collectorUrl;

	/**
	 * Paramétrage du service collecteur.
	 */
	public TTNCollectorClient() {
		Properties props = new Properties();
		try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
			if (input == null) {
				throw new RuntimeException("config.properties introuvable dans le classpath");
			}
			props.load(input);
			collectorUrl = props.getProperty("rust-collector.url");
			if (collectorUrl == null) {
				throw new RuntimeException("La clé 'rust-collector.url' est absente de config.properties");
			}
		} catch (IOException e) {
			throw new RuntimeException("Erreur lors du chargement de config.properties", e);
		}
	}
	
	/**
	 * Réalise un POST HTTP
	 * @param ttnMessage TTN content
	 * @return HTTP Code returned
	 */
	public Integer post(TTNCollectedMessage ttnMessage) {
		Integer http_code = 0;
		try {
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(ttnMessage);
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(collectorUrl))
				.header("Content-Type", "application/json; utf-8")
				.POST(BodyPublishers.ofString(json))
				.build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			http_code = response.statusCode();
		} catch (Exception e) {
			throw new RuntimeException("Erreur lors de l'appel POST au collecteur", e);
		}
		return http_code;
	}

	public String getCollectorUrl() {
		return collectorUrl;
	}

}