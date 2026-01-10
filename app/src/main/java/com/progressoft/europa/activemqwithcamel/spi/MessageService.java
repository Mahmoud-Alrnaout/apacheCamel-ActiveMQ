package com.progressoft.europa.activemqwithcamel.spi;

import com.progressoft.europa.activemqwithcamel.data.MessageRequest;
import org.springframework.http.ResponseEntity;

public interface MessageService {
     ResponseEntity<?> sendMessage(MessageRequest request);

}
