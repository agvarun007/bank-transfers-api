package com.varun.banking_transfers_api;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class ApiIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @Test
    void endToEnd_accounts_transfer_history() throws Exception {
        // create Alice
        MvcResult r1 = mvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Alice\",\"initialBalance\":\"100.00\"}"))
                .andReturn();
        JsonNode a = om.readTree(r1.getResponse().getContentAsString());
        String alice = a.get("accountId").asText();

        // create Bob
        MvcResult r2 = mvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Bob\",\"initialBalance\":\"50.00\"}"))
                .andReturn();
        JsonNode b = om.readTree(r2.getResponse().getContentAsString());
        String bob = b.get("accountId").asText();

        // transfer 25
        MvcResult r3 = mvc.perform(post("/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"fromAccountId\":\"" + alice + "\",\"toAccountId\":\"" + bob + "\",\"amount\":\"25.00\"}"))
                .andReturn();
        JsonNode t = om.readTree(r3.getResponse().getContentAsString());
        assertThat(t.get("status").asText()).isEqualTo("SUCCESS");
        assertThat(t.get("transactionId").asText()).hasSize(36);

        // balances
        JsonNode aBal = om.readTree(mvc.perform(get("/accounts/" + alice)).andReturn().getResponse().getContentAsString());
        JsonNode bBal = om.readTree(mvc.perform(get("/accounts/" + bob)).andReturn().getResponse().getContentAsString());
        assertThat(aBal.get("balance").asText()).isEqualTo("75.0");
        assertThat(bBal.get("balance").asText()).isEqualTo("75.0");

        // history
        String hist = mvc.perform(get("/accounts/" + alice + "/transactions"))
                .andReturn().getResponse().getContentAsString();
        JsonNode list = om.readTree(hist);
        assertThat(list.isArray()).isTrue();
        assertThat(list.size()).isGreaterThanOrEqualTo(1);
    }
}
