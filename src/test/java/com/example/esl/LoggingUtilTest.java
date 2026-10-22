package com.example.esl;

import com.example.esl.util.LoggingUtil;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoggingUtilTest {

    @Test
    void sendsRedactedJsonLogToConfiguredEndpoint() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        CountDownLatch logReceived = new CountDownLatch(1);
        AtomicReference<String> requestBody = new AtomicReference<>();
        server.createContext("/api/logs", exchange -> {
            requestBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            exchange.sendResponseHeaders(202, -1);
            exchange.close();
            logReceived.countDown();
        });
        server.start();

        try {
            LoggingUtil loggingUtil = new LoggingUtil(
                    JsonMapper.builder().build(),
                    "http://localhost:" + server.getAddress().getPort() + "/api/logs");
            loggingUtil.logRequest(
                    "/api/quoting",
                    "test-correlation-id",
                    JsonMapper.builder().build().readTree("""
                            {"customerName":"Alex Example","email":"alex@example.com"}
                            """));

            assertTrue(logReceived.await(5, TimeUnit.SECONDS), "Log event was not posted");
            assertTrue(requestBody.get().contains("\"event\":\"Request received\""));
            assertTrue(requestBody.get().contains("\"correlationId\":\"test-correlation-id\""));
            assertTrue(requestBody.get().contains("\"email\":\"[REDACTED]\""));
            assertFalse(requestBody.get().contains("alex@example.com"));
        } finally {
            server.stop(0);
        }
    }
}
