package org.personal.banking.recommender.controllers;

import org.personal.banking.recommender.dto.RecommendationResponse;
import org.personal.banking.recommender.service.RecommendationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recommendation")
public class RecommendationController {

    private final RecommendationService service;

    public RecommendationController(RecommendationService service) {
        this.service = service;
    }

    @GetMapping("/{userId}")
    public RecommendationResponse getRecommendation(@PathVariable String userId) {
        return new RecommendationResponse(
                userId,
                service.getRecommendations(userId)
        );
    }

}
