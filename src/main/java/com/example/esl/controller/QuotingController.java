package com.example.esl.controller;

import com.example.esl.dto.QuotingRequest;
import com.example.esl.dto.QuotingResponse;
import com.example.esl.service.QuotingService;
import com.example.esl.util.LoggingUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/quoting")
public class QuotingController {

    private final QuotingService quotingService;
    private final ObjectMapper objectMapper;
    private final LoggingUtil loggingUtil;

    public QuotingController(
            QuotingService quotingService,
            ObjectMapper objectMapper,
            LoggingUtil loggingUtil) {
        this.quotingService = quotingService;
        this.objectMapper = objectMapper;
        this.loggingUtil = loggingUtil;
    }

    @PostMapping
    public QuotingResponse createQuote(@RequestBody QuotingRequest request) {
        String correlationId = generateCorrelationId();
        loggingUtil.logRequest("/api/quoting", correlationId, objectMapper.valueToTree(request));
        QuotingResponse response = quotingService.createQuote(request, correlationId);
        loggingUtil.logResponse("/api/quoting", correlationId, objectMapper.valueToTree(response));
        return response;
    }

    private String generateCorrelationId() {
        return java.util.UUID.randomUUID().toString();
    }
}
