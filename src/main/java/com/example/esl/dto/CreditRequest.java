package com.example.esl.dto;

import java.math.BigDecimal;

public record CreditRequest(BigDecimal requestedAmount, int termMonths) {
}
