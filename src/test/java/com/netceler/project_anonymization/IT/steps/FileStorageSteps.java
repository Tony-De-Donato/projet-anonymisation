package com.netceler.project_anonymization.IT.steps;

import com.netceler.project_anonymization.IT.configurations.SpringCucumberTest;
import com.netceler.project_anonymization.fileStorage.FileStorageProperties;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

public class FileStorageSteps extends SpringCucumberTest {

    @Autowired
    private FileStorageProperties fileStorageProperties;

    private MultipartFile file;

    @Given("A configuration for file storage")
    public void cnfiguration() {
        fileStorageProperties.setDefaultLocation("src/test/resources");
        fileStorageProperties.setToAnonymizeLocation("src/test/resources");
        fileStorageProperties.setAnonymizedLocation("src/test/resources");
    }

    @When("I post a file {string} for anonymized")
    public void i_get_a_file_text_for_anonymized(String fileName) {
        final var restClient = RestClient.builder().baseUrl("http://localhost:" + port).build();
        file = restClient.post().uri("/anonymized/{fileName}", fileName).retrieve().body(MultipartFile.class);

        // TODO continue with give a file instead of file name
    }

    @Then("I should get the file {string} anonymized")
    public void i_should_get_the_file_text_anonymized(String fileName) {
        Assertions.assertThat(file).isNotNull();

        // TODO continue with a check of content of file
    }
}
