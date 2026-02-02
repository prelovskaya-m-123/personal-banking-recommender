package org.personal.banking.recommender.rules;

import org.personal.banking.recommender.dto.RecommendationDto;
import org.personal.banking.recommender.repository.RecommendationRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
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
            String recommendationId = userId + "_Invest500";
            return Optional.of(new RecommendationDto(
                    recommendationId,
                    "Invest 500",
                    "Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС) от нашего банка! Воспользуйтесь налоговыми льготами и начните инвестировать с умом. Пополните счет до конца года и получите выгоду в виде вычета на взнос в следующем налоговом периоде. Не упустите возможность разнообразить свой портфель, снизить риски и следить за актуальными рыночными тенденциями. Откройте ИИС сегодня и станьте ближе к финансовой независимости!"
            ));
        }

        return Optional.empty();

    }


}
