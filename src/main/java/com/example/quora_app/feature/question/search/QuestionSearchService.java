package com.example.quora_app.feature.question.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionSearchService {
    private final ElasticsearchClient elasticsearchClient;
    private static final String INDEX_NAME = "questions-java";

    public void indexQuestion(QuestionSearchDocument document) {
        try {
            elasticsearchClient.index(i -> i
                    .index(INDEX_NAME)
                    .id(document.getId().toString())
                    .document(document)
            );
            log.info("Successfully indexed question: {}", document.getId());
        } catch (IOException e) {
            log.error("Failed to index question to Elasticsearch", e);
            throw new RuntimeException("Indexing failed", e);
        }
    }

    public QuestionSearchDocument getQuestion(UUID questionId) {
        try {
            var response = elasticsearchClient.get(g -> g
                            .index(INDEX_NAME)
                            .id(questionId.toString()),
                    QuestionSearchDocument.class
            );

            if (!response.found()) {
                log.warn("Question not found in Elasticsearch with ID: {}", questionId);
                return null;
            }

            return response.source();

        } catch (IOException e) {
            log.error("Failed to fetch question from Elasticsearch", e);
            throw new RuntimeException("Fetch failed", e);
        }
    }

    public List<QuestionSearchDocument> searchByTitle(String searchText){
        try {

            var response = elasticsearchClient.search(s -> s
                            .index(INDEX_NAME)
                            .query(q -> q
                                    .match(m -> m
                                            .field("title")
                                            .query(searchText)
                                    )
                            ),
                    QuestionSearchDocument.class
            );

            List<QuestionSearchDocument> questionSearchDocument= response.hits()
                    .hits()
                    .stream()
                    .map(hit -> hit.source())
                    .toList();
            System.out.println("Found " + questionSearchDocument.size() + " questions in Elasticsearch"+questionSearchDocument);
            return questionSearchDocument;

        } catch (IOException e) {
            log.error("Failed to search question from Elasticsearch", e);
            throw new RuntimeException("search failed", e);
        }
    }

}
