package com.progressoft.europa.activemqwithcamel.pdl.data;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest {
    @NotBlank(message = "Content is required")
    private String content;
    @NotBlank(message = "Sender is required")
    private String sender;
}