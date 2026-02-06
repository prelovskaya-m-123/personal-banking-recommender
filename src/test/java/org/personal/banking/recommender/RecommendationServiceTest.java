package org.personal.banking.recommender;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.personal.banking.recommender.dto.RecommendationDto;
import org.personal.banking.recommender.h2.repository.RecommendationRepository;
import org.personal.banking.recommender.postgres.repository.DynamicRuleRepository;
import org.personal.banking.recommender.rules.RecommendationRules;
import org.personal.banking.recommender.service.RecommendationService;

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
}
