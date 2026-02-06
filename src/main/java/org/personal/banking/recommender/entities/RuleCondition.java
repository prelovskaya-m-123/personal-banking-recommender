package org.personal.banking.recommender.entities;

import jakarta.persistence.Embeddable;
import java.util.List;

@Embeddable
public class RuleCondition {

    private String query;
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
        return arguments;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }

    public boolean isNegate() {
        return negate;
    }

    public void setNegate(boolean negate) {
        this.negate = negate;
    }
}