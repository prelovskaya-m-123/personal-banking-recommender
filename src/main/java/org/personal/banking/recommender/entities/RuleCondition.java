package org.personal.banking.recommender.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Table;

import java.util.List;

@Embeddable
@Table(name = "rule_conditions")
public class RuleCondition {

    @Column(name = "query", nullable = false)
    private String query;

    @Column(name = "arguments")
    private List<String> arguments;

    private boolean negate;

    public RuleCondition() {
    }

    public RuleCondition(String query, List<String> arguments, boolean negate) {
        this.query = query;
        this.arguments = arguments;
        this.negate = negate;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<String> getArguments() {
        return arguments != null ? arguments : List.of();
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments != null ? arguments : List.of();
    }

    public boolean isNegate() {
        return negate;
    }

    public void setNegate(boolean negate) {
        this.negate = negate;
    }
}