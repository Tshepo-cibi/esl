package com.example.esl.dto;

import java.math.BigDecimal;

public record ProposalRequest(BigDecimal propertyPrice, BigDecimal deposit, int termMonths) {
}
