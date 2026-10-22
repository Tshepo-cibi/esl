package com.example.esl.dto;

import java.math.BigDecimal;

public record CreditResponse(
        String assessmentReference,
        String status,
        BigDecimal requestedAmount,
        int termMonths) {
}
