package org.personal.banking.recommender.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class RecommendationResponse {

    private String userId;
    private List<RecommendationDto> recommendations;


    public RecommendationResponse(
                    String userId,
                    List<RecommendationDto> recommendations) {

        this.userId = userId;
        this.recommendations = recommendations;

    }

    @JsonProperty("user_id")
    public String getUserId() {
        return userId;
    }

    public List<RecommendationDto> getRecommendations() {
        return recommendations;
    }

}
