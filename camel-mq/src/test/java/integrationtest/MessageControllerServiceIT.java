package integrationtest;

import com.progressoft.europa.activemqwithcamel.ActivemqpdlApplication;
import com.progressoft.europa.activemqwithcamel.data.MessageRequest;
import config.CamelTestConfig;
import org.apache.camel.Exchange;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = ActivemqpdlApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
@Import(CamelTestConfig.class)
class MessageControllerServiceIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BlockingQueue<Exchange> receivedMessages;

    @Test
    void shouldSendMessageAndConsumeIt() throws Exception {
        MessageRequest request = new MessageRequest();
        request.setSender("Mahmoud");
        request.setContent("Full Integration Test");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<MessageRequest> entity =
                new HttpEntity<>(request, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(
                        "/api/messages/send",
                        entity,
                        String.class
                );

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        Exchange exchange =
                receivedMessages.poll(5, TimeUnit.SECONDS);

        assertThat(exchange).isNotNull();

        MessageRequest received = exchange.getIn().getBody(MessageRequest.class);

        assertThat(received.getContent())
                .isEqualTo("Full Integration Test");

        assertThat(exchange.getIn().getHeader("correlationId"))
                .isNotNull();
    }
}
