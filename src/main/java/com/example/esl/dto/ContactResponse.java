package com.example.esl.dto;

public record ContactResponse(
        String contactReference,
        String status,
        String customerName,
        String email) {
}
