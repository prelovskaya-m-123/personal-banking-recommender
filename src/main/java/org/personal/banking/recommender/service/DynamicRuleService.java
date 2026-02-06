package org.personal.banking.recommender.service;

import org.personal.banking.recommender.entities.DynamicRule;
import org.personal.banking.recommender.postgres.repository.DynamicRuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DynamicRuleService {

    private final DynamicRuleRepository repository;

    public DynamicRuleService(DynamicRuleRepository repository) {
        this.repository = repository;
    }

    // 1. Добавление правила
    public DynamicRule createRule(DynamicRule rule) {
        return repository.save(rule);
    }

    // 2. Удаление правила
    public void deleteRule(UUID ruleId) {
        repository.deleteById(ruleId);
    }

    // 3. Получение всех правил
    public List<DynamicRule> getAllRules() {
        return repository.findAll();
    }
}
