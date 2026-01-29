package org.personal.banking.recommender.service;

import org.personal.banking.recommender.dto.RecommendationDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendationService {

    public List<RecommendationDto> getRecommendations(String userId) {
        return List.of();
    }

}
