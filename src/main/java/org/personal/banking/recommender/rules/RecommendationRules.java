package org.personal.banking.recommender.rules;

import org.personal.banking.recommender.dto.RecommendationDto;

import java.util.Optional;
import java.util.UUID;

public interface RecommendationRules {

    Optional<RecommendationDto> check(UUID userId);

}
