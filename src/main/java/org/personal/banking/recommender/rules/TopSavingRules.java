package org.personal.banking.recommender.rules;

import org.personal.banking.recommender.dto.RecommendationDto;
import org.personal.banking.recommender.repository.RecommendationRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public class TopSavingRules implements RecommendationRules {

    private final RecommendationRepository repository;

    public TopSavingRules(RecommendationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationDto> check(UUID userId) {

        boolean hasDebit = repository.hasProductType(userId, "DEBIT");

        BigDecimal debitDeposits = repository.sumByProductAndTransaction(
                userId, "DEBIT", "DEPOSIT");

        BigDecimal savingDeposits = repository.sumByProductAndTransaction(
                userId, "SAVING", "DEPOSIT");

        BigDecimal debitWithdrawals = repository.sumByProductAndTransaction(
                userId, "DEBIT", "WITHDRAWAL");

        // Сравниваем BigDecimal через compareTo
        if (hasDebit &&
                (debitDeposits.compareTo(new BigDecimal("50000")) >= 0 ||
                        savingDeposits.compareTo(new BigDecimal("50000")) >= 0) &&
                debitDeposits.compareTo(debitWithdrawals) > 0
        ) {
            return Optional.of(new RecommendationDto(
                    "id из БД",
                    "Top Saving",
                    "Откройте свой накопительный счёт."
            ));
        }

        return Optional.empty();
    }

}
