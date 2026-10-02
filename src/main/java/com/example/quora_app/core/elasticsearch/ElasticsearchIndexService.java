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
public class ElasticsearchIndexService {
    private final ElasticsearchClient elasticsearchClient;

    @PostConstruct
    public void initializeSchema() {
        try {
            createQuestionsIndex();
            log.info("✅ Elasticsearch index 'questions-java' verified/created successfully.");
        } catch (IOException e) {
            log.error("❌ Failed to initialize Elasticsearch index", e);
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    public boolean indexExists(String indexName) throws IOException {
        return elasticsearchClient.indices()
                .exists(e -> e.index(indexName))
                .value();
    }
    public void createQuestionsIndex() throws IOException {
        String indexName = "questions-java";
        if (indexExists(indexName)) {
            return;
        }
        elasticsearchClient.indices()
                .create(c -> c
                        .index(indexName)
                        .mappings(m -> m
                                .properties("id", p -> p.keyword(k -> k))
                                .properties("title", p -> p.text(t -> t))
                                .properties("content", p -> p.text(t -> t))
                                .properties("userId", p -> p.keyword(k -> k))
                                .properties("username", p -> p.keyword(k -> k))
                                .properties("tags", p -> p.keyword(k -> k))
                                .properties("createdAt", p -> p.date(d -> d))
                                .properties("version", p -> p.long_(l -> l))
                                .properties("updatedAt", p -> p.date(d -> d))
                        )
                );
    }
}
