package com.project_anonymization.IT.steps;

import com.project_anonymization.IT.configurations.SpringCucumberTest;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;
import org.json.JSONArray;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@AutoConfigureMockMvc
public class DictionarySteps extends SpringCucumberTest {

    private ResponseEntity<String> responseEntity;

    @When("I search the regex rule by name like {string}")
    public void i_search_the_regex_rule_by_name(String ruleName) {
        RestTemplate restTemplate = new RestTemplate();
        responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/getDictByRuleName/" + ruleName + "/false", HttpMethod.GET,
                null, String.class);
    }

    @Then("I should get a regex rule named like {string}")
    public void i_should_get_a_regex_rule_named_like(String rule) {
        JSONArray jsonArray = new JSONArray(responseEntity.getBody());
        Assertions.assertThat(jsonArray.getJSONObject(0).getString("name").contains(rule));
    }

    @When("I search the regex rule by file name like {string}")
    public void i_search_the_regex_rule_by_file_name(String fileName) {
        RestTemplate restTemplate = new RestTemplate();
        responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/getDictByFileName/" + fileName + "/false", HttpMethod.GET,
                null, String.class);
    }

    @Then("I should get a regex rule from my file name search")
    public void i_should_get_a_regex_rule_with_the_file_name_like() {
        JSONArray jsonArray = new JSONArray(responseEntity.getBody());
        Assertions.assertThat(!jsonArray.getJSONObject(0).getString("name").isEmpty());
    }

    @When("I get all regex rules")
    public void i_get_all_regex_rules() {
        RestTemplate restTemplate = new RestTemplate();
        responseEntity = restTemplate.exchange("http://localhost:" + port + "/getAllDict", HttpMethod.GET,
                null, String.class);
    }

    @Then("I should get at least 2 different regex rules")
    public void i_should_get_at_least_2_different_regex_rules() {
        JSONArray jsonArray = new JSONArray(responseEntity.getBody());
        Assertions.assertThat(jsonArray.length() >= 2);
        for (int i = 0; i < jsonArray.length(); i++) {
            for (int j = i + 1; j < jsonArray.length(); j++) {
                Assertions.assertThat(!jsonArray.getJSONObject(i).equals(jsonArray.getJSONObject(j)));
            }
        }
    }

}
