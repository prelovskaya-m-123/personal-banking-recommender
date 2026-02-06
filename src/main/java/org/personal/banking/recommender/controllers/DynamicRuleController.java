package org.personal.banking.recommender.controllers;

import org.personal.banking.recommender.entities.DynamicRule;
import org.personal.banking.recommender.service.DynamicRuleService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rule")
public class DynamicRuleController {

    private final DynamicRuleService service;

    public DynamicRuleController(DynamicRuleService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DynamicRule createRule(@RequestBody DynamicRule rule) {
        return service.createRule(rule);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRule(@PathVariable UUID id) {
        service.deleteRule(id);
    }

    @GetMapping
    public List<DynamicRule> getAllRules() {
        return service.getAllRules();
    }
}

