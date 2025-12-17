package com.progressoft.europa.activemqwithcamel.pdl.config;

import org.apache.camel.CamelContext;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CamelConfig implements CamelContextConfiguration {

    @Override
    public void beforeApplicationStart(CamelContext context) {
    }

    @Override
    public void afterApplicationStart(CamelContext context) {
    }
}