package com.progressoft.europa.activemqwithcamel.store;

import com.progressoft.europa.activemqwithcamel.data.MessageResponse;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ResponseStore {
    private final Map<String, MessageResponse> responses = new ConcurrentHashMap<>();

    public void store(String correlationId, MessageResponse response) {
        if(correlationId != null && response != null)
            responses.put(correlationId, response);
    }


    public MessageResponse poll(String correlationId, long timeoutMs) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            if (responses.containsKey(correlationId)) {
                return responses.remove(correlationId);
            }
            Thread.sleep(100);
        }
        return null;
    }


}