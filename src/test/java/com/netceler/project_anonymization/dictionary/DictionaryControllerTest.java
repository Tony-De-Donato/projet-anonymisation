package com.netceler.project_anonymization.dictionary;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = { DictionaryController.class, DictionaryService.class })
@AutoConfigureMockMvc
public class DictionaryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    DictionaryService dictionaryService;

    @Autowired
    DictionaryController dictionaryController;

    @Test
    void should_return_a_json_list_of_dict_when_getDictByRuleName_is_called() throws Exception {
        Mockito.when(dictionaryService.dictListToJsonString(any())).thenReturn("toto");
        mockMvc.perform(get("/getDictByRuleName/testDict/true"))
                .andExpect(status().isOk())
                .andExpect(content().string("toto"));
    }

    @Test
    void should_return_a_json_list_of_dict_when_getDictByFileName_is_called() throws Exception {
        mockMvc.perform(get("/getDictByFileName/test_dict.json"))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{\"1\":{\"regexp\":\"testToReplace\",\"name\":\"testDict\",\"replacement\":\"testReplaced\"}}"));
    }

    @Test
    void should_return_a_json_list_of_default_dict_when_getDictByRuleName_is_called() throws Exception {
        mockMvc.perform(get("/getAllDefaultDict"))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{\"1\":{\"regexp\":\"testToReplaceDefault\",\"name\":\"testDictDefault\",\"replacement\":\"testReplacedDefault\"}, \"2\":{\"regexp\":\"(?<=\\\\s|^)[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\\\.[A-Za-z]{2,}(?=\\\\s|$)\",\"name\":\"email\",\"replacement\":\"username@domain.com\"}}"));
    }

    @Test
    void should_return_a_json_list_of_dict_when_getAllDict_is_called() throws Exception {
        final MvcResult result = mockMvc.perform(get("/getAllDict")).andExpect(status().isOk()).andReturn();

        final String jsonResponse = result.getResponse().getContentAsString();
        Assertions.assertThat(jsonResponse)
                .contains(
                        "{\"regexp\":\"testToReplace\",\"name\":\"testDict\",\"replacement\":\"testReplaced\"}");
    }

    @Test
    void should_return_a_json_list_of_dict_like_the_name_given_when_getDictByRuleName_is_called_with_accurate_param_false()
            throws Exception {
        mockMvc.perform(get("/getDictByRuleName/mail/false"))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{\"1\":{\"regexp\":\"(?<=\\\\s|^)[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\\\.[A-Za-z]{2,}(?=\\\\s|$)\",\"name\":\"email\",\"replacement\":\"username@domain.com\"}}"));
    }

    @Test
    void should_return_an_empty_json_list_when_all_routes_are_called_with_an_unknown_name() throws Exception {
        mockMvc.perform(get("/getDictByRuleName/unknown/true"))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));

        mockMvc.perform(get("/getDictByFileName/unknown.json"))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));
    }

    @Test
    void should_return_a_well_formated_json_list_when_getDictByRuleName_is_called_with_accurate_param_false()
            throws Exception {
        final MvcResult result = mockMvc.perform(get("/getDictByFileName/test_dict.json"))
                .andExpect(status().isOk())
                .andReturn();

        final String jsonResponse = result.getResponse().getContentAsString();
        Assertions.assertThat(jsonResponse)
                .contains(
                        "{\"regexp\":\"(?<=\\\\s|^)[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\\\.[A-Za-z]{2,}(?=\\\\s|$)\",\"name\":\"email\",\"replacement\":\"username@domain.com\"}");

    }

}
