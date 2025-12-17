package com.progressoft.europa.activemqwithcamel.pdl.messageroute;

import com.progressoft.europa.activemqwithcamel.pdl.data.MessageRequest;
import com.progressoft.europa.activemqwithcamel.pdl.data.MessageResponse;
import com.progressoft.europa.activemqwithcamel.pdl.store.ResponseStore;
import com.progressoft.europa.activemqwithcamel.pdl.websockets.MessageWebSocketHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
@AllArgsConstructor
public class MessageRoutes extends RouteBuilder {

    private final ResponseStore responseStore;

    @Override
    public void configure() {
        from("jms:queue:sendMessage.queue")
                .routeId("ReceiveMessageFromJMS")
                .log("Received message from sendMessage.queue: ${body}")
                .log("----------------------------------------------------")
                .log("COPY THIS CorrelationID for your manual reply: ${header.correlationId}")
                .log("----------------------------------------------------");

        from("jms:queue:responseMessage.queue")
                .routeId("ReceiveResponseFromJMS")
                .log("Received raw text from queue: ${body}")
                .process(exchange -> {
                    // DEBUG: Print all headers to see what we got
                    Map<String, Object> headers = exchange.getIn().getHeaders();
                    log.info("All Headers Received: {}", headers);

                    // Try getting "correlationId" (custom)
                    String correlationId = exchange.getIn().getHeader("correlationId", String.class);

                    // Fallback: Try getting standard "JMSCorrelationID"
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

                        responseStore.store(correlationId, response);
                        MessageWebSocketHandler.broadcastMessage(correlationId, response);
                        log.info("Manual text response broadcasted. ID: {}", correlationId);
                    } else {
                        log.error("Failed! Missing Header. correlationId is null. Body: {}", responseContent);
                    }
                });
    }
}