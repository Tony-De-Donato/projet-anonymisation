package com.netceler.project_anonymization.IT.steps;

import com.netceler.project_anonymization.IT.configurations.SpringCucumberTest;
import com.netceler.project_anonymization.fileStorage.FileStorageProperties;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@AutoConfigureMockMvc
public class FileStorageSteps extends SpringCucumberTest {

    @Autowired
    private FileStorageProperties fileStorageProperties;

    private ResponseEntity<String> responseEntity;

    @Given("A configuration for file storage")
    public void configuration() {
        fileStorageProperties.setAnonymizedLocation("src/test/resources/fileStorage/filesAnonymized/");
    }

    @When("I post two files {string} and {string} for anonymize")
    public void i_post_a_file_text_for_anonymize(String fileName, String dictName) throws IOException {

        final var fileToSend = new File("src/test/resources/filesForTests/" + fileName);
        final var dictFile = new File("src/test/resources/filesForTests/" + dictName);

        MockMultipartFile fileMultipartFile = new MockMultipartFile("file", fileName, "text/plain",
                new FileInputStream(fileToSend));
        MockMultipartFile dictMultipartFile = new MockMultipartFile("dictionary", dictName,
                "application/json", new FileInputStream(dictFile));

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileMultipartFile.getResource());
        body.add("dictionary", dictMultipartFile.getResource());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        responseEntity = restTemplate.exchange("http://localhost:" + port + "/anonymize/", HttpMethod.POST,
                requestEntity, String.class);
    }

    @Then("I should get the file anonymized and his content should be {string}")
    public void i_should_get_the_file_text_anonymized(String content) {
        Assertions.assertThat(responseEntity).isNotNull();
        Assertions.assertThat(new JSONObject(responseEntity.getBody()).get("dict")).isNotNull();
        Assertions.assertThat(new JSONObject(responseEntity.getBody()).get("content")).isEqualTo(content);
    }
}
