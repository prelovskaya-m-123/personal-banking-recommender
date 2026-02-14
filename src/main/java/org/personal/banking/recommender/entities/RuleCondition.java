package org.personal.banking.recommender.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "rule_conditions")
public class RuleCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rule_id", nullable = false)
    private DynamicRule rule;

    @Column(name = "query", nullable = false, length = 50)
    private String query;

    // Применение конвертера для преобразования List<String> ↔ TEXT[]
    @Convert(converter = org.personal.banking.recommender.converter.StringArrayConverter.class)
    @Column(name = "arguments", columnDefinition = "TEXT[]")
    private List<String> arguments = new ArrayList<>();

    @Column(name = "negate", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean negate = false;;

    public RuleCondition() {
    }

    public RuleCondition(String query, List<String> arguments, boolean negate) {
        this.query = query;
        this.arguments = arguments;
        this.negate = negate;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public DynamicRule getRule() {
        return rule;
    }

    public void setRule(DynamicRule rule) {
        this.rule = rule;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments != null ? arguments : new ArrayList<>();;
    }

    public boolean isNegate() {
        return negate;
    }

    public void setNegate(boolean negate) {
        this.negate = negate;
    }
}