package org.personal.banking.recommender;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.personal.banking.recommender.dto.RecommendationDto;
import org.personal.banking.recommender.entities.DynamicRule;
import org.personal.banking.recommender.h2.repository.RecommendationRepository;
import org.personal.banking.recommender.postgres.repository.DynamicRuleRepository;
import org.personal.banking.recommender.rules.RecommendationRules;
import org.personal.banking.recommender.service.RecommendationService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RecommendationServiceTest {

    private RecommendationService service;
    private List<RecommendationRules> recommendationRules;

    @Mock
    private DynamicRuleRepository dynamicRuleRepository;

    @Mock
    private RecommendationRepository recommendationRepository;

    @InjectMocks
    private RecommendationService recommendationService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        recommendationRules = new ArrayList<>();

        service = new RecommendationService(
                recommendationRules,
                dynamicRuleRepository,
                recommendationRepository
        );
    }

    private DynamicRule createDynamicRule() {
        return new DynamicRule(
                "Dynamic Product",
                UUID.randomUUID(),
                "Text",
                List.of(new DynamicRule.RuleCondition(
                        "USER_OF",
                        List.of("CREDIT"),
                        false
                ))
        );
    }

    @Test
    public void testGetRecommendationsWhenNoRuleApplies() {
        String userId = "b7d7f2bc-dc5b-4f84-b135-f92b13a476bb";
        UUID uuid = UUID.fromString(userId);

        RecommendationRules mockRule1 = mock(RecommendationRules.class);
        RecommendationRules mockRule2 = mock(RecommendationRules.class);


        when(mockRule1.check(uuid)).thenReturn(Optional.empty());
        when(mockRule2.check(uuid)).thenReturn(Optional.empty());

        recommendationRules.add(mockRule1);
        recommendationRules.add(mockRule2);

        // Мокируем динамические правила (возвращаем пустой список)
        when(dynamicRuleRepository.findAll()).thenReturn(new ArrayList<>());


        List<RecommendationDto> result = service.getRecommendations(uuid);

        assertTrue(result.isEmpty(), "Ожидался пустой список рекомендаций, так как ни одно правило не сработало");
    }

    @Test
    public void testGetRecommendationsWhenOneRuleApplies() {
        String userId = "b7d7f2bc-dc5b-4f84-b135-f92b13a476bb";
        UUID uuid = UUID.fromString(userId);

        RecommendationDto recommendation = new RecommendationDto("565", "Иван", "Кредит");
        recommendation.setText("Рекомендуемый продукт");

        RecommendationRules firstRule = mock(RecommendationRules.class);
        when(firstRule.check(uuid)).thenReturn(Optional.of(recommendation));


        RecommendationRules secondRule = mock(RecommendationRules.class);
        when(secondRule.check(uuid)).thenReturn(Optional.empty());


        recommendationRules.add(firstRule);
        recommendationRules.add(secondRule);

        when(dynamicRuleRepository.findAll()).thenReturn(new ArrayList<>());


        List<RecommendationDto> result = service.getRecommendations(uuid);


        assertEquals(1, result.size(), "Ожидалась одна рекомендация от сработавшего правила");
        assertEquals(recommendation.getText(), result.get(0).getText(), "Текст рекомендации не совпадает");
    }

    @Test
    public void testGetRecommendationsWhenMultipleRulesApply() {
        String userId = "b7d7f2bc-dc5b-4f84-b135-f92b13a476bb";
        UUID uuid = UUID.fromString(userId);

        RecommendationDto rec1 = new RecommendationDto("678", "Иван", "INVEST");
        rec1.setText("Первый рекомендуемый продукт");


        RecommendationDto rec2 = new RecommendationDto("567", "Глеб", "CREDIT");
        rec2.setText("Второй рекомендуемый продукт");


        RecommendationRules firstRule = mock(RecommendationRules.class);
        when(firstRule.check(uuid)).thenReturn(Optional.of(rec1));


        RecommendationRules secondRule = mock(RecommendationRules.class);
        when(secondRule.check(uuid)).thenReturn(Optional.of(rec2));


        recommendationRules.add(firstRule);
        recommendationRules.add(secondRule);

        when(dynamicRuleRepository.findAll()).thenReturn(new ArrayList<>());

        List<RecommendationDto> result = service.getRecommendations(uuid);

        assertEquals(2, result.size(), "Ожидались две рекомендации от сработавших правил");
        assertEquals(rec1.getText(), result.get(0).getText(), "Текст первой рекомендации не совпадает");
        assertEquals(rec2.getText(), result.get(1).getText(), "Текст второй рекомендации не совпадает");
    }

    @Test
    public void testInvalidUUIDThrowsException() {

        String invalidUserId = "invalid-user-id";

        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> service.getRecommendations(UUID.fromString(invalidUserId)),
                "Метод должен выбросить исключение при некорректном ID");

        assertNotNull(exception, "Исключение не должно быть null");
        assertTrue(exception.getMessage().contains("Invalid UUID string"),
                "Сообщение об ошибке должно содержать описание проблемы с форматом UUID");
    }

    @Test
    void shouldApplyUserOfDynamicRule() {
        UUID userId = UUID.randomUUID();

        DynamicRule rule = new DynamicRule(
                "Credit Product",
                UUID.randomUUID(),
                "Text",
                List.of(new DynamicRule.RuleCondition(
                        "USER_OF",
                        List.of("CREDIT"),
                        false
                ))
        );

        when(dynamicRuleRepository.findAll()).thenReturn(List.of(rule));
        when(recommendationRepository.hasProductType(userId, "CREDIT")).thenReturn(true);

        List<RecommendationDto> result = service.getRecommendations(userId);

        assertEquals(1, result.size());
    }

    @Test
    void shouldApplyNegateForUserOf() {
        UUID userId = UUID.randomUUID();

        DynamicRule rule = new DynamicRule(
                "Credit Product",
                UUID.randomUUID(),
                "Text",
                List.of(new DynamicRule.RuleCondition(
                        "USER_OF",
                        List.of("CREDIT"),
                        true
                ))
        );

        when(dynamicRuleRepository.findAll()).thenReturn(List.of(rule));
        when(recommendationRepository.hasProductType(userId, "CREDIT")).thenReturn(true);

        List<RecommendationDto> result = service.getRecommendations(userId);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldApplyActiveUserOfRule() {
        UUID userId = UUID.randomUUID();

        DynamicRule rule = new DynamicRule(
                "Active Debit",
                UUID.randomUUID(),
                "Text",
                List.of(new DynamicRule.RuleCondition(
                        "ACTIVE_USER_OF",
                        List.of("DEBIT"),
                        false
                ))
        );

        when(dynamicRuleRepository.findAll()).thenReturn(List.of(rule));
        when(recommendationRepository.countTransactionsByProduct(userId, "DEBIT"))
                .thenReturn(6);

        List<RecommendationDto> result = service.getRecommendations(userId);

        assertEquals(1, result.size());
    }

    @Test
    void shouldApplyTransactionSumCompareRule() {
        UUID userId = UUID.randomUUID();

        DynamicRule rule = new DynamicRule(
                "Big Deposit",
                UUID.randomUUID(),
                "Text",
                List.of(new DynamicRule.RuleCondition(
                        "TRANSACTION_SUM_COMPARE",
                        List.of("DEBIT", "DEPOSIT", ">", "1000"),
                        false
                ))
        );

        when(dynamicRuleRepository.findAll()).thenReturn(List.of(rule));
        when(recommendationRepository.sumByProductAndTransaction(userId, "DEBIT", "DEPOSIT"))
                .thenReturn(new BigDecimal("2000"));

        List<RecommendationDto> result = service.getRecommendations(userId);

        assertEquals(1, result.size());
    }

    @Test
    void shouldApplyDepositWithdrawCompareRule() {
        UUID userId = UUID.randomUUID();

        DynamicRule rule = new DynamicRule(
                "Deposit Greater",
                UUID.randomUUID(),
                "Text",
                List.of(new DynamicRule.RuleCondition(
                        "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW",
                        List.of("DEBIT", ">"),
                        false
                ))
        );

        when(dynamicRuleRepository.findAll()).thenReturn(List.of(rule));
        when(recommendationRepository.sumByProductAndTransaction(userId, "DEBIT", "DEPOSIT"))
                .thenReturn(new BigDecimal("5000"));
        when(recommendationRepository.sumByProductAndTransaction(userId, "DEBIT", "WITHDRAWAL"))
                .thenReturn(new BigDecimal("1000"));

        List<RecommendationDto> result = service.getRecommendations(userId);

        assertEquals(1, result.size());
    }

    @Test
    void shouldNotApplyRuleIfOneConditionFails() {
        UUID userId = UUID.randomUUID();

        DynamicRule rule = new DynamicRule(
                "Complex Rule",
                UUID.randomUUID(),
                "Text",
                List.of(
                        new DynamicRule.RuleCondition(
                                "USER_OF",
                                List.of("CREDIT"),
                                false
                        ),
                        new DynamicRule.RuleCondition(
                                "ACTIVE_USER_OF",
                                List.of("DEBIT"),
                                false
                        )
                )
        );

        when(dynamicRuleRepository.findAll()).thenReturn(List.of(rule));
        when(recommendationRepository.hasProductType(userId, "CREDIT")).thenReturn(true);
        when(recommendationRepository.countTransactionsByProduct(userId, "DEBIT")).thenReturn(1); // меньше 5

        List<RecommendationDto> result = service.getRecommendations(userId);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldThrowExceptionForUnknownQuery() {
        UUID userId = UUID.randomUUID();

        DynamicRule rule = new DynamicRule(
                "Invalid",
                UUID.randomUUID(),
                "Text",
                List.of(new DynamicRule.RuleCondition(
                        "UNKNOWN_QUERY",
                        List.of(),
                        false
                ))
        );

        when(dynamicRuleRepository.findAll()).thenReturn(List.of(rule));

        assertThrows(IllegalArgumentException.class,
                () -> service.getRecommendations(userId));
    }
}
