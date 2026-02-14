package org.personal.banking.recommender.service;

import org.personal.banking.recommender.dto.RecommendationDto;
import org.personal.banking.recommender.rules.RecommendationRules;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final List<RecommendationRules> rules;

    public RecommendationService(List<RecommendationRules> rules) {
        this.rules = rules;
    }


    public List<RecommendationDto> getRecommendations(UUID userId) {

        return rules.stream()
                .map(rule -> {
                    try {
                        return rule.check(userId);
                    } catch (Exception e) {
                        System.err.println("Ошибка в правиле " + rule.getClass().getSimpleName() + ": " + e.getMessage());
                        return Optional.empty();
                    }
                })
                .flatMap(opt -> ((Optional<RecommendationDto>) opt).stream())
                .collect(Collectors.toList());

    }
}
