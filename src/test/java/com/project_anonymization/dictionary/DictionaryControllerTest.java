package com.project_anonymization.dictionary;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Testcontainers
public class DictionaryControllerTest {

    @Container
    @ServiceConnection
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16.3");

    @Autowired
    MockMvc mockMvc;

    @Autowired
    DictionaryController dictionaryController;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void should_return_a_json_list_of_dict_when_getDictByRuleName_is_called() throws Exception {

        final List<Dictionary> defaultDict = new ArrayList<>();
        defaultDict.add(new Dictionary("testDict", "testToReplace", "testReplaced"));

        mockMvc.perform(get("/getDictByRuleName/testDict/true"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(defaultDict)));
    }

    @Test
    void should_return_a_json_list_of_dict_when_getDictByFileName_is_called() throws Exception {
        final Dictionary defaultDict = new Dictionary("testDict", "testToReplace", "testReplaced");

        MvcResult result = mockMvc.perform(get("/getDictByFileName/test_dict.json/true"))
                .andExpect(status().isOk())
                .andReturn();

        final String jsonResponse = result.getResponse().getContentAsString();
        Assertions.assertThat(jsonResponse).contains(objectMapper.writeValueAsString(defaultDict));
    }

    @Test
    void should_return_a_json_list_of_default_dict_when_getDictByRuleName_is_called() throws Exception {
        final List<Dictionary> defaultDict = new ArrayList<>();
        defaultDict.add(new Dictionary("testDictDefault", "testToReplaceDefault", "testReplacedDefault"));
        defaultDict.add(
                new Dictionary("email", "(?<=\\s|^)[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}(?=\\s|$)",
                        "username@domain.com"));

        mockMvc.perform(get("/getAllDefaultDict"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(defaultDict)));
    }

    @Test
    void should_return_a_json_list_of_dict_when_getAllDict_is_called() throws Exception {
        final MvcResult result = mockMvc.perform(get("/getAllDict")).andExpect(status().isOk()).andReturn();

        final String jsonResponse = result.getResponse().getContentAsString();
        Assertions.assertThat(jsonResponse)
                .contains(
                        "{\"name\":\"testDict\",\"regexp\":\"testToReplace\",\"replacement\":\"testReplaced\"}");
    }

    @Test
    void should_return_a_json_list_of_dict_like_the_name_given_when_getDictByRuleName_is_called_with_accurate_param_false()
            throws Exception {
        final List<Dictionary> defaultDict = new ArrayList<>();
        defaultDict.add(
                new Dictionary("email", "(?<=\\s|^)[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}(?=\\s|$)",
                        "username@domain.com"));
        MvcResult result = mockMvc.perform(get("/getDictByRuleName/mail/false"))
                .andExpect(status().isOk())
                .andReturn();

        final String jsonResponse = result.getResponse().getContentAsString();
        Assertions.assertThat(jsonResponse).contains(objectMapper.writeValueAsString(defaultDict));

    }

    @Test
    void should_return_an_empty_json_list_when_all_routes_are_called_with_an_unknown_name() throws Exception {
        mockMvc.perform(get("/getDictByRuleName/unknown/true"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        mockMvc.perform(get("/getDictByFileName/unknown.json/true"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void should_return_a_well_formated_json_list_when_getDictByFileName_is_called() throws Exception {
        final MvcResult result = mockMvc.perform(get("/getDictByFileName/test_dict.json/true"))
                .andExpect(status().isOk())
                .andReturn();

        final String jsonResponse = result.getResponse().getContentAsString();
        Assertions.assertThat(jsonResponse)
                .contains(
                        "{\"name\":\"testDict\",\"regexp\":\"testToReplace\",\"replacement\":\"testReplaced\"}");
    }

}
