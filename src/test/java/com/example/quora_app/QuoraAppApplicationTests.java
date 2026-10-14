package com.example.quora_app;

import com.example.quora_app.core.elasticsearch.ElasticsearchHealthService;
import com.example.quora_app.core.elasticsearch.ElasticsearchIndexService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class QuoraAppApplicationTests {
	@MockitoBean
	private ElasticsearchIndexService elasticsearchIndexService;

	@MockitoBean
	private ElasticsearchHealthService elasticsearchHealthService;

	@Test
	void contextLoads() {
	}

}
