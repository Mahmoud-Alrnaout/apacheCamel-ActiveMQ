package com.progressoft.europa.activemqwithcamel.service;

import com.progressoft.europa.activemqwithcamel.data.MessageRequest;
import com.progressoft.europa.activemqwithcamel.spi.MessageService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {

    private final ProducerTemplate producerTemplate;
    @Value("${app.queue.send}")
    private String sendMessage;


    public ResponseEntity<?> sendMessage(MessageRequest request) {
        String correlationId = UUID.randomUUID().toString();
        try {
            producerTemplate.sendBodyAndHeaders(
                    sendMessage,
                    request,
                    Map.of("correlationId", correlationId)
            );
            log.info("Message sent to sendMessage.queue with correlationId: {}", correlationId);
            return ResponseEntity.ok(Map.of("correlationId", correlationId));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Failed to send message");
        }
    }


}
