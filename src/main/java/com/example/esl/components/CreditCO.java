package com.example.esl.components;

import com.example.esl.dto.CreditRequest;
import com.example.esl.dto.CreditResponse;
import com.example.esl.util.LoggingUtil;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;


@Component
public class CreditCO {

    private final ObjectMapper objectMapper;
    private final LoggingUtil loggingUtil;

    public CreditCO(ObjectMapper objectMapper, LoggingUtil loggingUtil) {
        this.objectMapper = objectMapper;
        this.loggingUtil = loggingUtil;
    }

    public CreditResponse requestAssessment(CreditRequest request, String correlationId) {
        loggingUtil.logRequest("/api/credits", correlationId, objectMapper.valueToTree(request));

        CreditResponse response = new CreditResponse(
                java.util.UUID.randomUUID().toString(),
                "PENDING",
                request.requestedAmount(),
                request.termMonths());
        loggingUtil.logResponse("/api/credits", correlationId, objectMapper.valueToTree(response));
        return response;

    }
}
