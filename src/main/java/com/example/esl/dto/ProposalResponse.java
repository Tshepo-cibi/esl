package com.example.esl.dto;

import java.math.BigDecimal;

public record ProposalResponse(
        String proposalReference,
        String status,
        BigDecimal financedAmount,
        int termMonths) {
}
