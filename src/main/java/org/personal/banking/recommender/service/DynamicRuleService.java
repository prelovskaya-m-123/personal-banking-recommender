package org.personal.banking.recommender.service;

import org.personal.banking.recommender.entities.DynamicRule;
import org.personal.banking.recommender.entities.RuleCondition;
import org.personal.banking.recommender.postgres.repository.DynamicRuleRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class DynamicRuleService {

    private final DynamicRuleRepository repository;
    private static final Logger logger = LoggerFactory.getLogger(DynamicRuleService.class);


    public DynamicRuleService(DynamicRuleRepository repository) {this.repository = repository;
    }

    // 1. Добавление правила
    public DynamicRule createRule(DynamicRule rule) {
        try {
            logger.info("Начинаем сохранение нового правила. ID правила: {}", rule.getId());
            DynamicRule savedRule = repository.save(rule);
            logger.info("Правило успешно сохранено. ID: {}", savedRule.getId());
            return savedRule;
        } catch (Exception e) {
            logger.error("Ошибка при сохранении правила. ID: {}. Сообщение: {}",
                    rule.getId(), e.getMessage(), e);
            throw e;
        }
    }

    // 2. Удаление правила
    public void deleteRule(UUID ruleId) {
        try {
            logger.info("Начинаем удаление правила. ID: {}", ruleId);
            if (!repository.existsById(ruleId)) {
                logger.warn("Правило с ID {} не найдено в БД. Пропускаем удаление.", ruleId);
                return;
            }
            repository.deleteById(ruleId);
            logger.info("Правило с ID {} успешно удалено.", ruleId);
        } catch (Exception e) {
            logger.error("Ошибка при удалении правила с ID {}. Сообщение: {}",
                    ruleId, e.getMessage(), e);
            throw e;
        }
    }

    // 3. Получение всех правил
    public List<DynamicRule> getAllRules() {
        try {
            logger.info("Начинаем загрузку всех правил из БД");
            List<DynamicRule> rules = repository.findAll();
            for (DynamicRule rule : rules) {
                if (rule.getConditions() == null) {
                    rule.setConditions(Collections.emptyList());
                }
                for (RuleCondition condition : rule.getConditions()) {
                    if (condition.getArguments() == null) {
                        condition.setArguments(new ArrayList<>());
                    }
                }
            }

            logger.info("Загружено {} правил из БД", rules.size());
            return rules;
        } catch (Exception e) {
            logger.error("Критическая ошибка при загрузке правил из БД. Сообщение: {}",
                    e.getMessage(), e);
            throw e;
        }
    }
}
