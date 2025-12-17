package com.progressoft.europa.activemqwithcamel.pdl.service;

import com.progressoft.europa.activemqwithcamel.pdl.data.MessageRequest;
import com.progressoft.europa.activemqwithcamel.pdl.data.MessageResponse;
import com.progressoft.europa.activemqwithcamel.pdl.store.ResponseStore;
import com.progressoft.europa.activemqwithcamel.pdl.websockets.MessageWebSocketHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class MessageService {

    private final ProducerTemplate producerTemplate;


    public String sendMessage(MessageRequest request) {
        String correlationId = UUID.randomUUID().toString();
        try {
            producerTemplate.sendBodyAndHeaders(
                    "jms:queue:sendMessage.queue",
                    request,
                    Map.of("correlationId", correlationId)
            );
            log.info("Message sent to sendMessage.queue with correlationId: {}", correlationId);
        } catch (Exception e) {
            log.error("Error sending message: {}", e.getMessage());
        }
        return correlationId;
    }


}
