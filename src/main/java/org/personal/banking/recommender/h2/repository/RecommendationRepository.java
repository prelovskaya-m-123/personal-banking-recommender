package org.personal.banking.recommender.h2.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.UUID;

@Repository
public class RecommendationRepository {

    private final JdbcTemplate jdbcTemplate;

    public RecommendationRepository(@Qualifier("h2ReadOnlyDataSource") DataSource h2DataSource) {
        this.jdbcTemplate = new JdbcTemplate(h2DataSource);
    }

    @Cacheable("hasProductType")
    public boolean hasProductType(UUID userId, String productType) {
        String sql = """
        SELECT COUNT(*)
        FROM TRANSACTIONS t
        JOIN PRODUCTS p ON t.product_id = p.id
        WHERE t.user_id = ? AND p.type = ?
    """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, productType);
        return count != null && count > 0;
    }

    @Cacheable("sumByProductAndTransaction")
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

    @Cacheable("countTransactionsByProduct")
    public int countTransactionsByProduct(UUID userId, String productType) {
        String sql = """
        SELECT COUNT(*)
        FROM TRANSACTIONS t
        JOIN PRODUCTS p ON t.product_id = p.id
        WHERE t.user_id = ? AND p.type = ?
    """;
        return jdbcTemplate.queryForObject(sql, Integer.class, userId, productType);
    }
}
