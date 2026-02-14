package org.personal.banking.recommender.rules;

import org.personal.banking.recommender.dto.RecommendationDto;
import org.personal.banking.recommender.repository.RecommendationRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
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
            String recommendationId = userId + "_TopSaving";
            return Optional.of(new RecommendationDto(
                    recommendationId,
                    "Top Saving",
                    "Откройте свою собственную «Копилку» с нашим банком! «Копилка» — это уникальный банковский инструмент, который поможет вам легко и удобно накапливать деньги на важные цели. Больше никаких забытых чеков и потерянных квитанций — всё под контролем!\n" +
                            "\n" +
                            "Преимущества «Копилки»:\n" +
                            "\n" +
                            "Накопление средств на конкретные цели. Установите лимит и срок накопления, и банк будет автоматически переводить определенную сумму на ваш счет.\n" +
                            "\n" +
                            "Прозрачность и контроль. Отслеживайте свои доходы и расходы, контролируйте процесс накопления и корректируйте стратегию при необходимости.\n" +
                            "\n" +
                            "Безопасность и надежность. Ваши средства находятся под защитой банка, а доступ к ним возможен только через мобильное приложение или интернет-банкинг.\n" +
                            "\n" +
                            "Начните использовать «Копилку» уже сегодня и станьте ближе к своим финансовым целям!."
            ));
        }

        return Optional.empty();
    }

}