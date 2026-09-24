package com.example.quora_app.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "elasticsearch")
public record ElasticsearchProperties(
        String url,
        String username,
        String password
) {}