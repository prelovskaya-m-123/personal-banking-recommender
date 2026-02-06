package org.personal.banking.recommender.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Value("${cache.ttl-minutes:10}")
    private int ttlMinutes;

    @Value("${cache.max-size:1000}")
    private int maxSize;

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        // Задаём имена кешей
        cacheManager.setCacheNames(List.of(
                "hasProductType",
                "sumByProductAndTransaction",
                "countTransactionsByProduct"
        ));

        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(ttlMinutes, TimeUnit.MINUTES)
                .maximumSize(maxSize)
                .recordStats());

        return cacheManager;
    }
}
