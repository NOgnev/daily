package com.klaxon.daily.client;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAiClientConfig {

    @Value("${client.openai.api-key}")
    private String apiKey;

    @Bean
    public RequestInterceptor openAiAuthInterceptor() {
        return request -> {
            request.header("Authorization", "Bearer " + apiKey);
            request.header("Content-Type", "application/json");
        };
    }

}
