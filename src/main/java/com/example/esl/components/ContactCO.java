package com.example.esl.components;

import com.example.esl.dto.ContactRequest;
import com.example.esl.dto.ContactResponse;
import com.example.esl.util.LoggingUtil;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class ContactCO {

    private final ObjectMapper objectMapper;
    private final LoggingUtil loggingUtil;

    public ContactCO(ObjectMapper objectMapper, LoggingUtil loggingUtil) {
        this.objectMapper = objectMapper;
        this.loggingUtil = loggingUtil;
    }

    public ContactResponse createContact(ContactRequest request, String correlationId) {
        loggingUtil.logRequest("/api/contacts", correlationId, objectMapper.valueToTree(request));
        ContactResponse response = new ContactResponse(
                java.util.UUID.randomUUID().toString(),
                "RECEIVED",
                request.customerName(),
                request.email());
        loggingUtil.logResponse("/api/contacts", correlationId, objectMapper.valueToTree(response));
        return response;
    }
}
