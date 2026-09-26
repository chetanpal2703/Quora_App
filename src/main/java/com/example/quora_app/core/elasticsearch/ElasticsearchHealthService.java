package com.example.quora_app.core.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ElasticsearchHealthService {

    private final ElasticsearchClient elasticsearchClient;

    @PostConstruct
    public void testConnectionOnStartup() {
        try {
            boolean isHealthy = elasticsearchClient.ping().value();
            if (isHealthy) {
                log.info("🚀 Elasticsearch connection status: CONNECTED ✅");
            } else {
                log.warn("⚠️ Elasticsearch connection status: PING FAILED");
            }
        } catch (IOException e) {
            log.error("❌ Failed to connect to Elasticsearch. Is the Docker container running?", e);
        }
    }
}