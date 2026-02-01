package org.personal.banking.recommender.rules;

import org.personal.banking.recommender.dto.RecommendationDto;
import org.personal.banking.recommender.repository.RecommendationRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public class Invest500Rules implements RecommendationRules {
    private final RecommendationRepository repository;

    public Invest500Rules(RecommendationRepository repository) {
        this.repository = repository;

    }

    @Override
    public Optional<RecommendationDto> check(UUID userId) {
        boolean hasDebit = repository.hasProductType(userId, "DEBIT");
        boolean hasInvest = repository.hasProductType(userId, "INVEST");
        BigDecimal sumSaving = repository
                .sumByProductAndTransaction(userId, "SAVING", "DEPOSIT");

        if (hasDebit && !hasInvest && sumSaving.compareTo(BigDecimal.valueOf(1000)) > 0) {
            return Optional.of(new RecommendationDto(
                    "id из базы данных",
                    "Invest 500",
                    "..."
            ));
        }

        return Optional.empty();

    }


}
