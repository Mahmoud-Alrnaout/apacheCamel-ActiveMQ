package com.progressoft.europa.activemqwithcamel.controller;


import com.progressoft.europa.activemqwithcamel.data.MessageRequest;
import com.progressoft.europa.activemqwithcamel.spi.MessageService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@Valid @RequestBody MessageRequest request) {
       return messageService.sendMessage(request);
    }

}
