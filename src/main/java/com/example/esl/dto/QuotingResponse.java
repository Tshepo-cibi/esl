package com.example.esl.dto;

public record QuotingResponse(
        String quoteReference,
        String status,
        ContactResponse contact,
        CreditResponse credit,
        ProposalResponse proposal) {
}
