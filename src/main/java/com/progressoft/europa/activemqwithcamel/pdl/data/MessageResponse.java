package com.progressoft.europa.activemqwithcamel.pdl.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private String correlationId;
    private String content;
    private String status;
    private long timestamp;
}