package org.personal.banking.recommender;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.personal.banking.recommender.controllers.RecommendationController;
import org.personal.banking.recommender.dto.RecommendationDto;
import org.personal.banking.recommender.service.RecommendationService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RecommendationControllerTest {

    @Mock
    private RecommendationService service;

    @InjectMocks
    private RecommendationController controller;

    private MockMvc mvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    /**
     * Тестирует успешное получение рекомендаций по корректному UUID.
     */
    @Test
    public void shouldReturnUserRecommendationsSuccessfully() throws Exception {

        UUID userId = UUID.fromString("b7d7f2bc-dc5b-4f84-b135-f92b13a476bb");
        String userIdStr = userId.toString();

        List<RecommendationDto> recommendations = List.of(
                new RecommendationDto("1", "Инвест", "Откройте ИИС"),
                new RecommendationDto("2", "Кредит", "Получите кредит")
        );

        given(service.getRecommendations(userId)).willReturn(recommendations);

        ResultActions result = mvc.perform(get("/recommendation/" + userIdStr)
                .contentType(MediaType.APPLICATION_JSON));


        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id", equalTo(userIdStr)))
                .andExpect(jsonPath("$.recommendations.length()", equalTo(2)))
                .andExpect(jsonPath("$.recommendations[0].id", equalTo("1")))
                .andExpect(jsonPath("$.recommendations[0].name", equalTo("Инвест")))
                .andExpect(jsonPath("$.recommendations[0].text", equalTo("Откройте ИИС")))
                .andExpect(jsonPath("$.recommendations[1].id", equalTo("2")))
                .andExpect(jsonPath("$.recommendations[1].name", equalTo("Кредит")))
                .andExpect(jsonPath("$.recommendations[1].text", equalTo("Получите кредит")));
    }

    /**
     * Тестирует случай, когда рекомендаций нет (пустой список).
     */
    @Test
    public void shouldHandleCaseWhenNoRecommendationsFound() throws Exception {
        UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-556642440000");
        String userIdStr = userId.toString();


        given(service.getRecommendations(userId)).willReturn(List.of());

        ResultActions result = mvc.perform(get("/recommendation/" + userIdStr)
                .contentType(MediaType.APPLICATION_JSON));


        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id", equalTo(userIdStr)))
                .andExpect(jsonPath("$.recommendations.length()", equalTo(0)));
    }


    /**
     * Тестирует обработку несуществующего endpoint — должен вернуть 404.
     */
    @Test
    public void shouldVerifyControllerEndpointIsCorrectlyMapped() throws Exception {
        ResultActions result = mvc.perform(get("/nonexistent-endpoint")
                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNotFound());
    }
}
