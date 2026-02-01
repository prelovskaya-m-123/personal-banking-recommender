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

    public BigDecimal sumByProductAndTransaction(
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

    public BigDecimal sumDebitDeposits(UUID userId) {
        return sumByProductAndTransaction(userId, "DEBIT", "DEPOSIT");
    }

    /**
     * Сумма всех трат по продуктам DEBIT
     */
    public BigDecimal sumDebitWithdrawals(UUID userId) {
        return sumByProductAndTransaction(userId, "DEBIT", "WITHDRAWAL");
    }

    /**
     * Сумма всех пополнений по продуктам SAVING
     */
    public BigDecimal sumSavingDeposits(UUID userId) {
        return sumByProductAndTransaction(userId, "SAVING", "DEPOSIT");
    }

    /**
     * Проверяет, использует ли пользователь кредитные продукты
     */
    public boolean hasCredit(UUID userId) {
        return hasProductType(userId, "CREDIT");
    }

    /**
     * Проверяет, использует ли пользователь инвестиционные продукты
     */
    public boolean hasInvest(UUID userId) {
        return hasProductType(userId, "INVEST");
    }
}
