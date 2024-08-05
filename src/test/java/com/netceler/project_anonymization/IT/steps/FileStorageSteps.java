package com.netceler.project_anonymization.IT.steps;

import com.netceler.project_anonymization.IT.configurations.SpringCucumberTest;
import com.netceler.project_anonymization.fileStorage.FileStorageProperties;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.io.File;
import java.io.IOException;

public class FileStorageSteps extends SpringCucumberTest {

    @Autowired
    private FileStorageProperties fileStorageProperties;

    private RestClient.RequestBodySpec returnedFile;

    @Given("A configuration for file storage")
    public void configuration() {
        fileStorageProperties.setToAnonymizeLocation("src/test/resources/fileStorage/filesToAnonymize/");
        fileStorageProperties.setAnonymizedLocation("src/test/resources/fileStorage/filesAnonymized/");
    }

    @When("I post two files {string} and {string} for anonymize")
    public void i_post_a_file_text_for_anonymize(String fileName, String dictName) throws IOException {

        final var fileToSend = new File("src/test/resources/filesForTests/" + fileName);
        final var dictFile = new File("src/test/resources/filesForTests/" + dictName);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileToSend);
        body.add("dictionary", dictFile);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body);

        returnedFile = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build()
                .post()
                .uri("/anonymize/")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(requestEntity);

    }

    @Then("I should get the file {string} anonymized")
    public void i_should_get_the_file_text_anonymized(String fileName) {
        Assertions.assertThat(returnedFile).isNotNull();
        if (!returnedFile.toString().equals("test_anonymized.txt")) {
            throw new AssertionError("The file is not the expected one : " + returnedFile);
        }
        // TODO continue with a check of content of file
    }
}
