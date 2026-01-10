package com.progressoft.europa.activemqwithcamel.processor;

import com.progressoft.europa.activemqwithcamel.data.MessageResponse;
import com.progressoft.europa.activemqwithcamel.store.ResponseStore;
import com.progressoft.europa.activemqwithcamel.websockets.MessageWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class ResponseMessageProcessor implements Processor {
    private final ResponseStore responseStore;

    @Override
    public void process(Exchange exchange) {
        Map<String, Object> headers = exchange.getIn().getHeaders();
        log.info("All Headers Received: {}", headers);

        String correlationId = exchange.getIn().getHeader("correlationId", String.class);
        if (correlationId == null) {
            correlationId = exchange.getIn().getHeader("JMSCorrelationID", String.class);
        }

        String responseContent = exchange.getIn().getBody(String.class);

        if (correlationId != null && responseContent != null) {
            MessageResponse response = new MessageResponse();
            response.setCorrelationId(correlationId);
            response.setContent(responseContent);
            response.setStatus("SUCCESS_MANUAL");
            response.setTimestamp(System.currentTimeMillis());

            responseStore.store(correlationId,response);
            MessageWebSocketHandler.broadcastMessage(correlationId, response);
            log.info("Manual text response broadcasted. ID: {}", correlationId);
        } else {
            log.error("Failed! Missing Header. correlationId is null. Body: {}", responseContent);
        }
    }
}
