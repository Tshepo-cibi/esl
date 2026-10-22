package com.example.esl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class EslApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void createsQuoteThroughComponents() throws Exception {
        mockMvc.perform(post("/api/quoting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerName": "Alex Example",
                                  "email": "alex@example.com",
                                  "propertyPrice": 1200000.00,
                                  "deposit": 200000.00,
                                  "termMonths": 240
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quoteReference").isNotEmpty())
                .andExpect(jsonPath("$.status").value("RECEIVED"))
                .andExpect(jsonPath("$.contact.customerName").value("Alex Example"))
                .andExpect(jsonPath("$.credit.status").value("PENDING"))
                .andExpect(jsonPath("$.credit.requestedAmount").value(1000000.00))
                .andExpect(jsonPath("$.proposal.status").value("CREATED"))
                .andExpect(jsonPath("$.proposal.financedAmount").value(1000000.00));
    }
}
