package com.progressoft.europa.activemqwithcamel.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MessageResponse {
    private String correlationId;
    private String content;
    private String status;
    private long timestamp;
}