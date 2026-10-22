package com.example.esl.service;

import com.example.esl.components.ContactCO;
import com.example.esl.components.CreditCO;
import com.example.esl.components.ProposalCO;
import com.example.esl.dto.ContactRequest;
import com.example.esl.dto.ContactResponse;
import com.example.esl.dto.CreditRequest;
import com.example.esl.dto.CreditResponse;
import com.example.esl.dto.ProposalRequest;
import com.example.esl.dto.ProposalResponse;
import com.example.esl.dto.QuotingRequest;
import com.example.esl.dto.QuotingResponse;
import org.springframework.stereotype.Service;

@Service
public class QuotingService {

    private final ContactCO contactCO;
    private final CreditCO creditCO;
    private final ProposalCO proposalCO;

    public QuotingService(ContactCO contactCO, CreditCO creditCO, ProposalCO proposalCO) {
        this.contactCO = contactCO;
        this.creditCO = creditCO;
        this.proposalCO = proposalCO;
    }

    public QuotingResponse createQuote(QuotingRequest request, String correlationId) {
        ContactResponse contact = contactCO.createContact(
                new ContactRequest(request.customerName(), request.email()), correlationId);

        var financedAmount = request.propertyPrice().subtract(request.deposit());
        CreditResponse credit = creditCO.requestAssessment(
                new CreditRequest(financedAmount, request.termMonths()), correlationId);
        ProposalResponse proposal = proposalCO.createProposal(
                new ProposalRequest(request.propertyPrice(), request.deposit(), request.termMonths()), correlationId);

        return new QuotingResponse(
                java.util.UUID.randomUUID().toString(),
                "RECEIVED",
                contact,
                credit,
                proposal);
    }
}
