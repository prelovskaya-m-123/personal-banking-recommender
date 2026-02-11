package org.personal.banking.recommender;

import org.junit.jupiter.api.Test;
import org.personal.banking.recommender.controllers.DynamicRuleController;
import org.personal.banking.recommender.entities.DynamicRule;
import org.personal.banking.recommender.service.DynamicRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(DynamicRuleController.class)
@AutoConfigureMockMvc
public class DynamicRuleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DynamicRuleService dynamicRuleService;

    /// CREATE DYNAMIC RULE ///
    @Test
    void shouldCreateDynamicRule() throws Exception {
        String requestJson = """
    {
      "product_name": "Простой кредит",
      "product_id": "ab138afb-f3ba-4a93-b74f-0fcee86d447f",
      "product_text": "Текст рекомендации",
      "rule": [
        {
          "query": "USER_OF",
          "arguments": ["CREDIT"],
          "negate": true
        }
      ]
    }
    """;

        DynamicRule savedRule = new DynamicRule();
        savedRule.setId(UUID.randomUUID());
        savedRule.setProductName("Простой кредит");
        savedRule.setProductId(UUID.fromString(
                "ab138afb-f3ba-4a93-b74f-0fcee86d447f"));
        savedRule.setProductText("Текст рекомендации");

        when(dynamicRuleService.createRule(any(DynamicRule.class)))
                .thenReturn(savedRule);

        mockMvc.perform(post("/rule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.productName").value("Простой кредит"));
    }

    /// GET ALL DYNAMIC RULES ///
    @Test
    void shouldReturnAllDynamicRules() throws Exception {
        when(dynamicRuleService.getAllRules())
                .thenReturn(List.of(
                        new DynamicRule(),
                        new DynamicRule()
                ));

        mockMvc.perform(get("/rule"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())          // просто массив
                .andExpect(jsonPath("$.length()").value(2)); // длина массива = 2
    }

    /// DELETE RULE ///
    @Test
    void shouldDeleteDynamicRule() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(dynamicRuleService).deleteRule(id);

        mockMvc.perform(delete("/rule/{id}", id))
                .andExpect(status().isNoContent());

        verify(dynamicRuleService).deleteRule(id);
    }


}
