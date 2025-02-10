package com.queue.worker.configuration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .readTimeout(Duration.ofSeconds(3000))  // Connection timeout (time to establish a connection)
                .connectTimeout(Duration.ofSeconds(3000))    // Read timeout (time to read data after connection established)
                .build();
    }
}
