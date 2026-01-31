package org.personal.banking.recommender.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public class RecommendationRepository {

    private final JdbcTemplate jdbcTemplate;

    public RecommendationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean hasProductType(UUID userId, String productType) {
        String sql = """
        SELECT COUNT(*) 
        FROM transactions t
        JOIN products p ON t.product_id = p.id
        WHERE t.user_id = ?
          AND p.type = ?
    """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, productType);
        return count != null && count > 0;
    }

    public BigDecimal sumByProductTypeAndTransactionType(
            UUID userId,
            String productType,
            String transactionType
    ) {
        String sql = """
        SELECT COALESCE(SUM(t.amount), 0)
        FROM transactions t
        JOIN products p ON t.product_id = p.id
        WHERE t.user_id = ?
          AND p.type = ?
          AND t.type = ?
    """;

        return jdbcTemplate.queryForObject(
                sql,
                BigDecimal.class,
                userId,
                productType,
                transactionType
        );
    }


}
