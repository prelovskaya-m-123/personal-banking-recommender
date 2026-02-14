package org.personal.banking.recommender.postgres.repository;

import jakarta.persistence.Table;
import org.personal.banking.recommender.entities.DynamicRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DynamicRuleRepository extends JpaRepository<DynamicRule, UUID> {
}

