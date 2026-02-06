package org.personal.banking.recommender.controllers;

import org.personal.banking.recommender.dto.RecommendationResponse;
import org.personal.banking.recommender.service.RecommendationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;



import java.util.UUID;

@RestController
@RequestMapping("/recommendation")
public class RecommendationController {

    private final RecommendationService service;

    public RecommendationController(RecommendationService service) {
        this.service = service;
    }

    @GetMapping("/{userId}")
    public RecommendationResponse getRecommendation(@PathVariable String userId) {
        try {
            UUID uuid = UUID.fromString(userId);

            return new RecommendationResponse(
                    userId,
                    service.getRecommendations(uuid)
            );
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid userId format. Expected a valid UUID string.",
                    e
            );
        }
    }
}
