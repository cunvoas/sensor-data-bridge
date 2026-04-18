package com.github.cunvoas.clientcollector.client;

import com.github.cunvoas.clientcollector.model.TTNCollectedMessage;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class TTNCollectorClientTest {

    @Test
    public void testCollectorUrlLoaded() {
        TTNCollectorClient client = new TTNCollectorClient();
        assertNotNull(client.getCollectorUrl(), "collectorUrl doit être chargé depuis config.properties");
        assertTrue(client.getCollectorUrl().startsWith("http"), "collectorUrl doit commencer par http");
    }

    @Test
    public void testPostReturnsHttpCode() {
        TTNCollectorClient client = new TTNCollectorClient();
        // Création d'un message factice
        TTNCollectedMessage msg = new TTNCollectedMessage("appId", "devId", "devEui", new byte[]{1,2,3}, "{}", 1);
        // Le serveur cible doit être accessible pour un vrai test d'intégration
        // Ici on vérifie simplement que l'appel ne lève pas d'exception
        try {
            Integer code = client.post(msg);
            assertNotNull(code);
        } catch (Exception e) {
            // OK si le serveur n'est pas joignable, mais pas d'autre exception
            assertTrue(e.getMessage().contains("POST"));
        }
    }

    @Test
    public void testPostE2E() throws Exception {
        // Démarre un serveur HTTP local sur un port libre
        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/uplink", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                if ("POST".equals(exchange.getRequestMethod())) {
                    exchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write("OK".getBytes());
                    }
                } else {
                    exchange.sendResponseHeaders(405, 0);
                }
            }
        });
        server.start();
        int port = server.getAddress().getPort();
        String url = "http://localhost:" + port + "/uplink";

        // Instancie le client et modifie collectorUrl
        TTNCollectorClient client = new TTNCollectorClient();
        // Utilisation de la réflexion pour modifier collectorUrl
        java.lang.reflect.Field field = TTNCollectorClient.class.getDeclaredField("collectorUrl");
        field.setAccessible(true);
        field.set(client, url);
        TTNCollectedMessage msg = new TTNCollectedMessage("appId", "devId", "devEui", new byte[]{1,2,3}, "{}", 1);
        Integer code = client.post(msg);
        assertEquals(200, code);
        server.stop(0);
    }
}