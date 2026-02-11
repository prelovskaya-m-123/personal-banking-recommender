package org.personal.banking.recommender.h2.repository;

import org.springframework.cache.annotation.Cacheable;
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

    /**
     * Проверяет, использует ли пользователь продукт данного типа
     */
    @Cacheable(
            value = "hasProductType",
            key = "#userId.toString() + '_' + #productType"
    )
    public boolean hasProductType(UUID userId, String productType) {

        String sql = """
            SELECT COUNT(*)
            FROM TRANSACTIONS t
            JOIN PRODUCTS p ON t.product_id = p.id
            WHERE t.user_id = ?
              AND p.type = ?
        """;

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                userId,
                productType
        );

        return count != null && count > 0;
    }

    /**
     * Считает сумму транзакций по типу продукта и типу транзакции
     */
    @Cacheable(
            value = "sumByProductAndTransaction",
            key = "#userId.toString() + '_' + #productType + '_' + #transactionType"
    )
    public BigDecimal sumByProductAndTransaction(
            UUID userId,
            String productType,
            String transactionType
    ) {

        String sql = """
            SELECT COALESCE(SUM(t.amount), 0)
            FROM TRANSACTIONS t
            JOIN PRODUCTS p ON t.product_id = p.id
            WHERE t.user_id = ?
              AND p.type = ?
              AND t.type = ?
        """;

        BigDecimal result = jdbcTemplate.queryForObject(
                sql,
                BigDecimal.class,
                userId,
                productType,
                transactionType
        );

        return result != null ? result : BigDecimal.ZERO;
    }

    /**
     * Считает количество транзакций по типу продукта
     */
    @Cacheable(
            value = "countTransactionsByProduct",
            key = "#userId.toString() + '_' + #productType"
    )
    public int countTransactionsByProduct(UUID userId, String productType) {

        String sql = """
            SELECT COUNT(*)
            FROM TRANSACTIONS t
            JOIN PRODUCTS p ON t.product_id = p.id
            WHERE t.user_id = ?
              AND p.type = ?
        """;

        Integer result = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                userId,
                productType
        );

        return result != null ? result : 0;
    }


    public boolean hasCredit(UUID userId) {
        return hasProductType(userId, "CREDIT");
    }

    public boolean hasInvest(UUID userId) {
        return hasProductType(userId, "INVEST");
    }

    public BigDecimal sumDebitDeposits(UUID userId) {
        return sumByProductAndTransaction(userId, "DEBIT", "DEPOSIT");
    }

    public BigDecimal sumDebitWithdrawals(UUID userId) {
        return sumByProductAndTransaction(userId, "DEBIT", "WITHDRAWAL");
    }

    public BigDecimal sumSavingDeposits(UUID userId) {
        return sumByProductAndTransaction(userId, "SAVING", "DEPOSIT");
    }
}