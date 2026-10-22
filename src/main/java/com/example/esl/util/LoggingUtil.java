package com.example.esl.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;

@Component
public class LoggingUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingUtil.class);
    private static final String REDACTED = "[REDACTED]";

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final URI logUri;

    public LoggingUtil(
            ObjectMapper objectMapper,
            @Value("${app.logging.url:http://localhost:8081/api/logs}") String logUrl) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();
        this.logUri = URI.create(logUrl);
    }

    public void logRequest(String endpoint, String correlationId, JsonNode requestJson) {
        logJson("Request received", endpoint, correlationId, requestJson);
    }

    public void logResponse(String endpoint, String correlationId, JsonNode responseJson) {
        logJson("Response sent", endpoint, correlationId, responseJson);
    }

    private void logJson(String event, String endpoint, String correlationId, JsonNode payload) {
        JsonNode sanitizedPayload = Objects.requireNonNull(payload, "payload must not be null").deepCopy();
        redactSensitiveFields(sanitizedPayload);
        LOGGER.info("{}: endpoint={}, correlationId={}, payload={}", event, endpoint, correlationId, sanitizedPayload);

        ObjectNode logEntry = objectMapper.createObjectNode()
                .put("event", event)
                .put("endpoint", endpoint)
                .put("correlationId", correlationId)
                .set("payload", sanitizedPayload);

        HttpRequest request = HttpRequest.newBuilder(logUri)
                .timeout(Duration.ofSeconds(5))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(logEntry)))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                .whenComplete((response, error) -> {
                    if (error != null) {
                        LOGGER.error(
                                "Failed to send log entry: url={}, endpoint={}, correlationId={}",
                                logUri,
                                endpoint,
                                correlationId,
                                error);
                    } else if (response.statusCode() < 200 || response.statusCode() >= 300) {
                        LOGGER.error(
                                "Log service rejected entry: url={}, status={}, endpoint={}, correlationId={}",
                                logUri,
                                response.statusCode(),
                                endpoint,
                                correlationId);
                    }
                });
    }

    private static void redactSensitiveFields(JsonNode node) {
        if (node.isObject()) {
            ObjectNode object = (ObjectNode) node;
            for (Map.Entry<String, JsonNode> field : object.properties()) {
                if (field.getKey().equalsIgnoreCase("customerName")
                        || field.getKey().equalsIgnoreCase("email")) {
                    object.put(field.getKey(), REDACTED);
                } else {
                    redactSensitiveFields(field.getValue());
                }
            }
        } else if (node.isArray()) {
            node.forEach(LoggingUtil::redactSensitiveFields);
        }
    }
}
