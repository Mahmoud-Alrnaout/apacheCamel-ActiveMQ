package com.progressoft.europa.activemqwithcamel.messageroute;


import com.progressoft.europa.activemqwithcamel.data.MessageResponse;
import com.progressoft.europa.activemqwithcamel.processor.ResponseMessageProcessor;
import com.progressoft.europa.activemqwithcamel.websockets.MessageWebSocketHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@AllArgsConstructor
public class MessageRoutes extends RouteBuilder {
    private final ResponseMessageProcessor responseMessageProcessor;


    @Override
    public void configure() {
        from("jms:queue:responseMessage.queue")
                .routeId("ReceiveResponseFromJMS")
                .log("Received raw text from queue: ${body}")
                .process(responseMessageProcessor);
    }
}