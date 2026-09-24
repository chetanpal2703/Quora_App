package com.example.quora_app.core.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest5_client.Rest5ClientTransport;
import co.elastic.clients.transport.rest5_client.low_level.Rest5Client;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.core5.http.HttpHost;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Configuration
@EnableConfigurationProperties(ElasticsearchProperties.class)
public class ElasticsearchConfig {
    @Bean
    public ElasticsearchClient elasticsearchClient(ElasticsearchProperties properties) {

        URI uri = URI.create(properties.url());
        HttpHost host = new HttpHost(uri.getScheme(), uri.getHost(), uri.getPort());

        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                new AuthScope(null, -1),
                new UsernamePasswordCredentials(properties.username(), properties.password().toCharArray())
        );

        // 1. Build the Rest5Client wrapper
        Rest5Client rest5Client = Rest5Client.builder(host)
                .setHttpClientConfigCallback(httpClientBuilder ->
                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                )
                .build();

        // 2. Pass the wrapper to the transport
        Rest5ClientTransport transport = new Rest5ClientTransport(
                rest5Client,
                new JacksonJsonpMapper()
        );

        return new ElasticsearchClient(transport);
    }
}
