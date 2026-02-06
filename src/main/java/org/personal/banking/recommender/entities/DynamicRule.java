package org.personal.banking.recommender.entities;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "dynamic_rules")
public class DynamicRule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "product_text")
    private String productText;

    @ElementCollection
    @CollectionTable(name = "rule_conditions", joinColumns = @JoinColumn(name = "rule_id"))
    private List<RuleCondition> conditions;


    public DynamicRule() {}

    public DynamicRule(String productName, UUID productId, String productText, List<RuleCondition> conditions) {
        this.productName = productName;
        this.productId = productId;
        this.productText = productText;
        this.conditions = conditions;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }
    public String getProductText() { return productText; }
    public void setProductText(String productText) { this.productText = productText; }
    public List<RuleCondition> getConditions() { return conditions; }
    public void setConditions(List<RuleCondition> conditions) { this.conditions = conditions; }


    @Embeddable
    public static class RuleCondition {
        private String query;
        private List<String> arguments;
        private boolean negate;

        public RuleCondition() {}
        public RuleCondition(String query, List<String> arguments, boolean negate) {
            this.query = query;
            this.arguments = arguments;
            this.negate = negate;
        }

        public String getQuery() { return query; }
        public void setQuery(String query) { this.query = query; }
        public List<String> getArguments() { return arguments; }
        public void setArguments(List<String> arguments) { this.arguments = arguments; }
        public boolean isNegate() { return negate; }
        public void setNegate(boolean negate) { this.negate = negate; }
    }
}
