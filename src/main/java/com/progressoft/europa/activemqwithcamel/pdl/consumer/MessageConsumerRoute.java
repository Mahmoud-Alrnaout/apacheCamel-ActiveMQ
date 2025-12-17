package com.progressoft.europa.activemqwithcamel.pdl.consumer;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumerRoute extends RouteBuilder {
    @Override
    public void configure() {
        from("jms:queue:order.queue")
            .routeId("ReceiveOrderFromJMS")
            .process(exchange -> {
                String consumedMessage = exchange.getMessage().getBody(String.class);
            })
            .log("Received order from order queue : ${body}");
    }
}