package org.personal.banking.recommender.rules;

import org.personal.banking.recommender.dto.RecommendationDto;
import org.personal.banking.recommender.repository.RecommendationRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
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
            String recommendationId = userId + "_SimpleCredit";
            return Optional.of(new RecommendationDto(
                    recommendationId,
                    "Простой кредит",
                    "Откройте мир выгодных кредитов с нами!\n" +
                            "\n" +
                            "Ищете способ быстро и без лишних хлопот получить нужную сумму? Тогда наш выгодный кредит — именно то, что вам нужно! Мы предлагаем низкие процентные ставки, гибкие условия и индивидуальный подход к каждому клиенту.\n" +
                            "\n" +
                            "Почему выбирают нас:\n" +
                            "\n" +
                            "Быстрое рассмотрение заявки. Мы ценим ваше время, поэтому процесс рассмотрения заявки занимает всего несколько часов.\n" +
                            "\n" +
                            "Удобное оформление. Подать заявку на кредит можно онлайн на нашем сайте или в мобильном приложении.\n" +
                            "\n" +
                            "Широкий выбор кредитных продуктов. Мы предлагаем кредиты на различные цели: покупку недвижимости, автомобиля, образование, лечение и многое другое.\n" +
                            "\n" +
                            "Не упустите возможность воспользоваться выгодными условиями кредитования от нашей компании!"
            ));
        }

        return Optional.empty();
    }
}
