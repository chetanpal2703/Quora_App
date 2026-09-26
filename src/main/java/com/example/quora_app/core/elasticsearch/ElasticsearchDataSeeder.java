package com.example.quora_app.core.elasticsearch;

import com.example.quora_app.feature.question.search.QuestionSearchDocument;
import com.example.quora_app.feature.question.search.QuestionSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ElasticsearchDataSeeder implements CommandLineRunner {
    private final QuestionSearchService searchService;
    @Override
    public void run(String... args) {
        log.info("Starting Elasticsearch test data insertion...");

        // Using your Lombok Builder, UUIDs, and Set.of()!
        QuestionSearchDocument testQuestion = QuestionSearchDocument.builder()
                .id(UUID.fromString("4e4bf8e1-1f19-4752-a4af-d080d378f7b3"))
                .title("How does JPA lazy loading work?- by spring")
                .content("I want to undecreateQuestionsIndexrstand how Hibernate loads lazy relationships.")
                .userId(UUID.fromString("a487a63b-020e-40b5-a087-4acb38793e2b"))
                .username("chetan pal")
                .tags(Set.of("java", "spring", "hibernate", "jpa")) // Fixed: Changed List to Set
                .createdAt(LocalDateTime.of(2026, 8, 20, 19, 7, 23))
                .build();

        searchService.indexQuestion(testQuestion);
        log.info("Test data insertion complete. Check Kibana!");
        searchService.searchByTitle("Spring");


        // Fetch the exact same document back out
        QuestionSearchDocument retrievedDoc = searchService.getQuestion(UUID.fromString("4e4bf8e1-1f19-4752-a4af-d080d378f7b3"));
        log.info("Successfully fetched from ES: {}", retrievedDoc.getTitle());
    }
}
