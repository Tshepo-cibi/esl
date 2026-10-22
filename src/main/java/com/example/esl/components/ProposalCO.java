package com.example.esl.components;

import com.example.esl.dto.ProposalRequest;
import com.example.esl.dto.ProposalResponse;
import com.example.esl.util.LoggingUtil;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;


@Component
public class ProposalCO {

    private final ObjectMapper objectMapper;
    private final LoggingUtil loggingUtil;

    public ProposalCO(ObjectMapper objectMapper, LoggingUtil loggingUtil) {
        this.objectMapper = objectMapper;
        this.loggingUtil = loggingUtil;
    }

    public ProposalResponse createProposal(ProposalRequest request, String correlationId) {
        loggingUtil.logRequest("/api/proposals", correlationId, objectMapper.valueToTree(request));

        ProposalResponse response = new ProposalResponse(
                java.util.UUID.randomUUID().toString(),
                "CREATED",
                request.propertyPrice().subtract(request.deposit()),
                request.termMonths());
        loggingUtil.logResponse("/api/proposals", correlationId, objectMapper.valueToTree(response));
        return response;
    }
}
