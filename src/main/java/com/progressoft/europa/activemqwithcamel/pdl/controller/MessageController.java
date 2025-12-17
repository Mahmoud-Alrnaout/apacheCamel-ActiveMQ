package com.progressoft.europa.activemqwithcamel.pdl.controller;

import com.progressoft.europa.activemqwithcamel.pdl.data.MessageRequest;
import com.progressoft.europa.activemqwithcamel.pdl.service.MessageService;
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
    public ResponseEntity<Map<String, String>> sendMessage(@Valid @RequestBody MessageRequest request) {
        return ResponseEntity.ok(Map.of("correlationId", messageService.sendMessage(request)));
    }

}
