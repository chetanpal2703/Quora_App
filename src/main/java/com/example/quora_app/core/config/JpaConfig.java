package com.example.quora_app.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "utcDateTimeProvider")
public class JpaConfig {
    @Bean
    public DateTimeProvider utcDateTimeProvider() {
        return () -> Optional.of(
                LocalDateTime.now(Clock.systemUTC())
        );
    }

}
