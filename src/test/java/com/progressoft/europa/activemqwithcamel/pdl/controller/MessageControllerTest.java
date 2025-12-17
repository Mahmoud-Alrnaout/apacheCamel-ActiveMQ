package com.progressoft.europa.activemqwithcamel.pdl.controller;

import com.progressoft.europa.activemqwithcamel.pdl.data.MessageRequest;
import com.progressoft.europa.activemqwithcamel.pdl.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MessageController.class)
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MessageService messageService;

    @Test
    void givenValidMessageRequest_whenSendMessage_thenReturnsCorrelationId() throws Exception {
        String expectedCorrelationId = "test-correlation-id-12345";
        when(messageService.sendMessage(any(MessageRequest.class)))
                .thenReturn(expectedCorrelationId);

        String requestBody = """
                {
                    "content": "Test message",
                    "sender": "user@example.com"
                }
                """;

        mockMvc.perform(post("/api/messages/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.correlationId").value(expectedCorrelationId));
    }

    @Test
    void givenInvalidMessageRequest_whenSendMessage_thenReturns4xxError() throws Exception {
        String invalidRequestBody = """
                {
                    "invalid": "field"
                }
                """;

        mockMvc.perform(post("/api/messages/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void givenEmptyMessageRequest_whenSendMessage_thenReturns4xxError() throws Exception {
        mockMvc.perform(post("/api/messages/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void givenNonJsonContentType_whenSendMessage_thenReturnsUnsupportedMediaType() throws Exception {
        mockMvc.perform(post("/api/messages/send")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("plain text"))
                .andExpect(status().isUnsupportedMediaType());
    }
}