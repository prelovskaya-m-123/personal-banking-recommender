package org.personal.banking.recommender;

import org.personal.banking.recommender.h2.repository.RecommendationRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import java.util.UUID;

import static org.mockito.Mockito.*;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RecommendationRepositoryCacheTest.TestConfig.class)
class RecommendationRepositoryCacheTest {

    @Configuration
    @EnableCaching
    static class TestConfig {

        @Bean
        public JdbcTemplate jdbcTemplate() {
            return Mockito.mock(JdbcTemplate.class);
        }

        @Bean
        public RecommendationRepository recommendationRepository(JdbcTemplate jdbcTemplate) {
            return new RecommendationRepository(jdbcTemplate);
        }

        @Bean
        public CacheManager cacheManager() {
            return new ConcurrentMapCacheManager(
                    "countTransactionsByProduct",
                    "hasProductType",
                    "sumByProductAndTransaction"
            );
        }
    }

    @Autowired
    private RecommendationRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)

    @Test
    void shouldCacheCountTransactions() {
        UUID userId = UUID.randomUUID();

        when(jdbcTemplate.queryForObject(
                anyString(),
                eq(Integer.class),
                any(),
                any()
        )).thenReturn(5);


        repository.countTransactionsByProduct(userId, "DEBIT");


        repository.countTransactionsByProduct(userId, "DEBIT");


        verify(jdbcTemplate, times(1))
                .queryForObject(anyString(), eq(Integer.class), any(), any());
    }

    @Test
    void shouldNotCacheDifferentArguments() {
        UUID userId = UUID.randomUUID();

        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), any(), any()))
                .thenReturn(5);

        repository.countTransactionsByProduct(userId, "DEBIT");
        repository.countTransactionsByProduct(userId, "CREDIT");

        verify(jdbcTemplate, times(2))
                .queryForObject(anyString(), eq(Integer.class), any(), any());
    }
}
