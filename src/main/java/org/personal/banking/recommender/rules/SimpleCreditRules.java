package org.personal.banking.recommender.rules;

import org.personal.banking.recommender.dto.RecommendationDto;
import org.personal.banking.recommender.repository.RecommendationRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public class SimpleCreditRules implements RecommendationRules {

    private final RecommendationRepository repository;

    public SimpleCreditRules(RecommendationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationDto> check(UUID userId) {

        boolean hasCredit = repository.hasProductType(userId, "CREDIT");

        BigDecimal debitDeposits = repository.sumByProductAndTransaction(
                userId, "DEBIT", "DEPOSIT");

        BigDecimal debitWithdrawals = repository.sumByProductAndTransaction(
                userId, "DEBIT", "WITHDRAWAL");

        if (!hasCredit &&
                debitDeposits.compareTo(debitWithdrawals) > 0 &&
                debitWithdrawals.compareTo(new BigDecimal("100000")) > 0
        ) {
            return Optional.of(new RecommendationDto(
                    "id из БД",
                    "Простой кредит",
                    "Откройте мир выгодных кредитов с нами!"
            ));
        }

        return Optional.empty();
    }
}
