package com.netceler.project_anonymization.fileStorage;

import org.apache.coyote.BadRequestException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.io.FileInputStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class FileStorageControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    FileStorageController fileStorageController;

    @Test
    void should_return_an_anonymised_file_when_post_url_is_called() throws Exception {
        final var file = new File("src/test/resources/filesForTests/test.txt");
        final var dictFile = new File("src/test/resources/filesForTests/dictTest.json");

        FileInputStream fileInputStream = new FileInputStream(file);
        FileInputStream dictFileInputStream = new FileInputStream(dictFile);

        MockMultipartFile fileMultipartFile = new MockMultipartFile("file", file.getName(), "text/plain",
                fileInputStream);
        MockMultipartFile dictMultipartFile = new MockMultipartFile("dictionary", dictFile.getName(),
                "application/json", dictFileInputStream);

        mockMvc.perform(multipart("/anonymize/").file(fileMultipartFile).file(dictMultipartFile))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{\"fileName\":\"test_anonymized.txt\", \"dict\":\"test_dict.json\", \"content\":\"Lorem ipsum username@domain.com dolor sit amet\"}"));

    }

    @Test
    void should_throw_a_400_error_when_post_url_is_called_with_no_dict_file() throws Exception {
        final var file = new File("src/test/resources/filesForTests/test.txt");
        FileInputStream fileInputStream = new FileInputStream(file);
        MockMultipartFile fileMultipartFile = new MockMultipartFile("file", file.getName(), "text/plain",
                fileInputStream);

        MvcResult result = mockMvc.perform(multipart("/anonymize/").file(fileMultipartFile))
                .andExpect(status().isBadRequest())
                .andReturn();

        String errorMessage = result.getResponse().getErrorMessage();
        Assertions.assertThat(errorMessage).isEqualTo("Required part 'dictionary' is not present.");

    }

    @Test
    void should_throw_an_error_when_post_url_is_called_with_no_file() throws Exception {
        final var dictFile = new File("src/test/resources/filesForTests/dictTest.json");
        FileInputStream dictFileInputStream = new FileInputStream(dictFile);
        MockMultipartFile dictMultipartFile = new MockMultipartFile("dictionary", dictFile.getName(),
                "application/json", dictFileInputStream);

        MvcResult result = mockMvc.perform(multipart("/anonymize/").file(dictMultipartFile))
                .andExpect(status().isBadRequest())
                .andReturn();

        String errorMessage = result.getResponse().getErrorMessage();
        Assertions.assertThat(errorMessage).isEqualTo("Required part 'file' is not present.");
    }

    @Test
    void should_return_a_dictionary_file_when_get_url_is_called_with_existing_dict_file() throws Exception {
        final var file = new File("src/test/resources/filesForTests/test.txt");
        final var dictFile = new File("src/test/resources/filesForTests/dictTest.json");

        FileInputStream fileInputStream = new FileInputStream(file);
        FileInputStream dictFileInputStream = new FileInputStream(dictFile);

        MockMultipartFile fileMultipartFile = new MockMultipartFile("file", file.getName(), "text/plain",
                fileInputStream);
        MockMultipartFile dictMultipartFile = new MockMultipartFile("dictionary", dictFile.getName(),
                "application/json", dictFileInputStream);

        mockMvc.perform(multipart("/anonymize/").file(fileMultipartFile).file(dictMultipartFile))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{\"fileName\":\"test_anonymized.txt\", \"dict\":\"test_dict.json\", \"content\":\"Lorem ipsum username@domain.com dolor sit amet\"}"));

        String dictReturn = "{\"1\" : { \"name\" : \"email\",\"regexp\" : \"(?<=\\\\s|^)[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\\\.[A-Za-z]{2,}(?=\\\\s|$)\",\"replacement\" : \"username@domain.com\"}}";

        mockMvc.perform(get("/getDictFile/test_dict.json"))
                .andExpect(status().isOk())
                .andExpect(content().json(dictReturn));

    }

    @Test
    void should_throw_a_bad_request_exception_when_get_url_is_called_with_a_non_existing_dict()
            throws Exception {
        Assertions.assertThatThrownBy(() -> mockMvc.perform(get("/getDictFile/test_non_existing_dict.txt"))
                        .andExpect(status().isBadRequest()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Could not read the content of the file: test_non_existing_dict.txt");
    }

    @Test
    void should_throw_a_bad_request_exception_when_get_url_is_called_with_a_non_dict_file() throws Exception {
        Assertions.assertThatThrownBy(
                        () -> mockMvc.perform(get("/getDictFile/test_nodict.txt")).andExpect(status().isBadRequest()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("This file is not a dictionary");
    }

    @Test
    void should_throws_a_404_error_when_file_name_outside_relative_path() throws Exception {
        mockMvc.perform(get("/getDict/../test_dict.txt")).andExpect(status().isNotFound());
    }

    @Test
    void should_throw_an_exception_when_post_url_is_called_with_empty_file() throws Exception {
        final var file = new File("src/test/resources/filesForTests/emptyTestFile.txt");
        final var dictFile = new File("src/test/resources/filesForTests/dictTest.json");

        FileInputStream fileInputStream = new FileInputStream(file);
        FileInputStream dictFileInputStream = new FileInputStream(dictFile);

        MockMultipartFile fileMultipartFile = new MockMultipartFile("file", file.getName(), "text/plain",
                fileInputStream);
        MockMultipartFile dictMultipartFile = new MockMultipartFile("dictionary", dictFile.getName(),
                "application/json", dictFileInputStream);

        Assertions.assertThatThrownBy(() -> mockMvc.perform(
                                multipart("/anonymize/").file(fileMultipartFile).file(dictMultipartFile))
                        .andExpect(status().isBadRequest()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Failed to store empty file");
    }

    @Test
    void should_throw_an_exception_when_post_url_is_called_with_empty_dict() throws Exception {
        final var file = new File("src/test/resources/filesForTests/test.txt");
        final var dictFile = new File("src/test/resources/filesForTests/emptyDict.json");

        FileInputStream fileInputStream = new FileInputStream(file);
        FileInputStream dictFileInputStream = new FileInputStream(dictFile);

        MockMultipartFile fileMultipartFile = new MockMultipartFile("file", file.getName(), "text/plain",
                fileInputStream);
        MockMultipartFile dictMultipartFile = new MockMultipartFile("dictionary", dictFile.getName(),
                "application/json", dictFileInputStream);

        Assertions.assertThatThrownBy(() -> mockMvc.perform(
                                multipart("/anonymize/").file(fileMultipartFile).file(dictMultipartFile))
                        .andExpect(status().isBadRequest()))
                .hasMessageContaining("Expected non empty content and dictionary");
    }
}
