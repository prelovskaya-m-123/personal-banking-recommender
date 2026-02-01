package org.personal.banking.recommender.service;

import org.personal.banking.recommender.dto.RecommendationDto;
import org.personal.banking.recommender.rules.RecommendationRules;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RecommendationService {

    private final List<RecommendationRules> rules;

    public RecommendationService(List<RecommendationRules> rules) {
        this.rules = rules;
    }


    public List<RecommendationDto> getRecommendations(String userId) {
        UUID uuid = UUID.fromString(userId);

        return rules.stream()
                .map(rule -> rule.check(uuid))
                .flatMap(Optional::stream)
                .toList();
    }


}
