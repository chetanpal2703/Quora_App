package com.example.quora_app.feature.question.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import com.example.quora_app.core.common.dto.PageResponse;
import com.example.quora_app.feature.question.enums.QuestionSearchSort;
import com.example.quora_app.feature.question.search.dto.QuestionSearchRequest;
import com.example.quora_app.feature.question.search.dto.QuestionSearchResult;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalTime;
import java.time.ZoneId;
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

    // REMOVED: throws IOException from the signature
    public PageResponse<QuestionSearchResult> search(QuestionSearchRequest request) {
        try {
            int from = request.getPage() * request.getSize();
            SearchRequest.Builder builder = new SearchRequest.Builder()
                    .index(INDEX_NAME)
                    .from(from)
                    .size(request.getSize())
                    .trackTotalHits(t -> t.enabled(true))
                    .query(buildQuery(request));

            applySorting(builder, request.getSort());

            var response = elasticsearchClient.search(
                    builder.build(),
                    QuestionSearchDocument.class
            );

            List<QuestionSearchResult> content =
                    response.hits()
                            .hits()
                            .stream()
                            .map(hit -> QuestionSearchResult.builder()
                                    .question(hit.source())
                                    .score(hit.score())
                                    .build())
                            .toList();

            long totalElements = response.hits()
                    .total()
                    .value();

            int totalPages = (int) ((totalElements + request.getSize() - 1) / request.getSize());
            boolean first = request.getPage() == 0;
            boolean last = totalPages == 0 || request.getPage() >= totalPages - 1;

            return PageResponse.<QuestionSearchResult>builder()
                    .content(content)
                    .page(request.getPage())
                    .size(request.getSize())
                    .totalElements(totalElements)
                    .totalPages(totalPages)
                    .first(first)
                    .last(last)
                    .build();

        } catch (IOException e) {
            // CATCH the network error and throw an unchecked RuntimeException instead
            log.error("Failed to execute Elasticsearch search query", e);
            throw new RuntimeException("Search service is currently unavailable", e);
        }
    }

    private Query buildQuery(QuestionSearchRequest request) {

        return Query.of(q -> q
                .bool(b -> {

                    if (request.getSearch() != null && !request.getSearch().isBlank()) {
                        b.must(m -> m
                                .multiMatch(mm -> mm
                                        .query(request.getSearch())
                                        .fields("title", "content")
                                )
                        );
                    }

                    if (request.getTag() != null && !request.getTag().isBlank()) {
                        b.filter(f -> f
                                .term(t -> t
                                        // FIX 3: Target the keyword sub-field for exact matches
                                        .field("tags")
                                        .value(request.getTag())
                                )
                        );
                    }

                    if (request.getFromDate() != null) {
                        b.filter(f -> f
                                .range(r -> r
                                        .date(d -> d
                                                .field("createdAt")
                                                // FIX 2a: Start safely at 00:00:00
                                                .gte(request.getFromDate().atStartOfDay(ZoneId.of("Asia/Kolkata"))
                                                        .toInstant().toString().toString())
                                        )
                                )
                        );
                    }

                    if (request.getToDate() != null) {
                        b.filter(f -> f
                                .range(r -> r
                                        .date(d -> d
                                                .field("createdAt")
                                                // FIX 2b: Push to the absolute end of the day (23:59:59.999)
                                                .lte(request.getToDate().atTime(LocalTime.MAX).atZone(ZoneId.of("Asia/Kolkata"))
                                                        .toInstant().toString())
                                        )
                                )
                        );
                    }
                    return b;
                })
        );
    }

    private void applySorting(SearchRequest.Builder builder, QuestionSearchSort sort) {
        // FIX 1: Prevent NullPointerException
        if (sort == null) {
            return;
        }

        switch (sort) {
            case NEWEST -> builder.sort(s -> s
                    .field(f -> f
                            .field("createdAt")
                            .order(SortOrder.Desc)
                    )
            );

            case OLDEST -> builder.sort(s -> s
                    .field(f -> f
                            .field("createdAt")
                            .order(SortOrder.Asc)
                    )
            );

            case RELEVANCE -> {
                // Elasticsearch defaults to relevance scoring automatically.
            }
        }
    }


    public void deleteQuestion(UUID questionId) throws IOException {
        elasticsearchClient.delete(d -> d
                .index(INDEX_NAME)
                .id(questionId.toString())
        );
    }
}
