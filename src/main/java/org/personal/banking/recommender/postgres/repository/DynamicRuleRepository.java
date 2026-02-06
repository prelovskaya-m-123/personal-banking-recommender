package org.personal.banking.recommender.postgres.repository;

import org.personal.banking.recommender.entities.DynamicRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DynamicRuleRepository extends JpaRepository<DynamicRule, UUID> {
}

