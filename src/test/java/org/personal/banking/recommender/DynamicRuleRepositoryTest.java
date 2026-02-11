package org.personal.banking.recommender;

import org.junit.jupiter.api.Test;
import org.personal.banking.recommender.entities.DynamicRule;
import org.personal.banking.recommender.postgres.repository.DynamicRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.liquibase.enabled=false"
})
public class DynamicRuleRepositoryTest {

    @Autowired
    private DynamicRuleRepository ruleRepository;

    private DynamicRule testDynamicRule() {
        DynamicRule rule = new DynamicRule();

        rule.setProductName("Простой кредит");
        rule.setProductId(UUID.randomUUID());
        rule.setProductText("Текст рекомендации");

        rule.setConditions(List.of(
                new DynamicRule.RuleCondition(
                        "USER_OF",
                        List.of("CREDIT"),
                        true
                )
        ));
        return rule;
    }

    @Test
    void shouldSaveAndFindRule() {
        DynamicRule rule = testDynamicRule();

        DynamicRule saved = ruleRepository.save(rule);

        assertThat(saved.getId()).isNotNull();

        List<DynamicRule> all = ruleRepository.findAll();

        assertThat(all).hasSize(1);
        assertThat(all.get(0).getProductName())
                .isEqualTo("Простой кредит");
    }

}
