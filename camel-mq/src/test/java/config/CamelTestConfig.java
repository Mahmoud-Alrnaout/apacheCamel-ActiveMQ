package config;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@TestConfiguration
public class CamelTestConfig {

    @Bean
    public BlockingQueue<Exchange> receivedMessages() {
        return new LinkedBlockingQueue<>();
    }

    @Bean
    public RouteBuilder testConsumerRoute(BlockingQueue<Exchange> receivedMessages) {
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("seda:sendMessage.queue")
                        .routeId("test-consumer-route")
                        .process(receivedMessages::add);
            }
        };
    }
}
