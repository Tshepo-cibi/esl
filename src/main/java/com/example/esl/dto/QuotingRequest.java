package com.example.esl.dto;

import java.math.BigDecimal;
import java.util.Objects;

public record QuotingRequest(
        String customerName,
        String email,
        BigDecimal propertyPrice,
        BigDecimal deposit,
        int termMonths) {

    public QuotingRequest {
        if (customerName == null || customerName.isBlank()) {
            throw new IllegalArgumentException("customerName is required");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email is required");
        }
        Objects.requireNonNull(propertyPrice, "propertyPrice is required");
        Objects.requireNonNull(deposit, "deposit is required");
        if (propertyPrice.signum() <= 0) {
            throw new IllegalArgumentException("propertyPrice must be greater than zero");
        }
        if (deposit.signum() < 0 || deposit.compareTo(propertyPrice) > 0) {
            throw new IllegalArgumentException("deposit must be between zero and propertyPrice");
        }
        if (termMonths <= 0) {
            throw new IllegalArgumentException("termMonths must be greater than zero");
        }
    }
}
