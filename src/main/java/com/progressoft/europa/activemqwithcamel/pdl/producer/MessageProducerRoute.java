//package com.progressoft.europa.activemqwithcamel.pdl.producer;
//
//import com.google.gson.Gson;
//import org.apache.camel.builder.RouteBuilder;
//import org.springframework.stereotype.Component;
//
//@Component
//public class MessageProducerRoute extends RouteBuilder {
//    private final Gson gson;
//
//    public MessageProducerRoute(Gson gson) {
//        this.gson = gson;
//    }
//
//    @Override
//    public void configure() {
//        from("timer:sendTimer?period=5000")
//                .routeId("SendMessageToJMS")
//                .process(exchange -> {
//                    exchange.getMessage().setBody("hello");
//                })
//                .to("jms:queue:sendMessage.queue")
//                .log("Message sent to sendMessage queue");
//    }
//}