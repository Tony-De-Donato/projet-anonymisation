package com.netceler.project_anonymization.anonymizer;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.regex.PatternSyntaxException;

@SpringBootTest
@Testcontainers
class AnonymizerServiceTest {

    @Autowired
    AnonymizerService anonymizerService;

    @Test
    void should_anonymize_string() {
        String content = "Lorem ipsum dolor sit amet";
        String dict = "{\"Lorem\":\"anonymized\"}";
        String result = anonymizerService.handleAnonymization(content, dict);

        Assertions.assertThat(result).isEqualTo("anonymized ipsum dolor sit amet");

    }

    @Test
    void should_anonymize_email() {
        String content = "Lorem ipsum dolor sit amet, email: anonymize@meee.com";
        String dict = "{\"(?<=\\s|^)[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}(?=\\s|$)\" : \"username@domain.com\"}";
        String result = anonymizerService.handleAnonymization(content, dict);

        Assertions.assertThat(result).isEqualTo("Lorem ipsum dolor sit amet, email: username@domain.com");
    }


    @Test
    void should_replace_ip_address() {
        String content = "Lorem ipsum dolor sit amet, ip: 192.168.1.1";
        String dict = "{\"(?<=\\s|^)(?:[0-9]{1,3}\\.){3}[0-9]{1,3}(?=\\s|$)\" : \"xx.xx.xx.xx\"}";
        String result = anonymizerService.handleAnonymization(content, dict);

        Assertions.assertThat(result).isEqualTo("Lorem ipsum dolor sit amet, ip: xx.xx.xx.xx");
    }


    @Test
    void should_anonymize_multiple_regex() {
        String content = "Lorem ipsum dolor sit amet, email: anonymise@me.com and ip: 192.168.1.1";
        String dict = "{\"(?<=\\s|^)[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}(?=\\s|$)\" : \"username@domain.com\", \"(?<=\\s|^)(?:[0-9]{1,3}\\.){3}[0-9]{1,3}(?=\\s|$)\" : \"xx.xx.xx.xx\"}";
        String result = anonymizerService.handleAnonymization(content, dict);

        Assertions.assertThat(result).isEqualTo("Lorem ipsum dolor sit amet, email: username@domain.com and ip: xx.xx.xx.xx");
    }


    @Test
    void should_throw_exception_when_invalid_dict() {
        String content = "Lorem ipsum dolor sit amet";
        String dict = "{\"Lorem\":anonymized\"}";

        Assertions.assertThatThrownBy(() -> anonymizerService.handleAnonymization(content, dict))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Expected");
    }


    @Test
    void should_throw_exception_when_invalid_regex() {
        String content = "Lorem ipsum dolor sit amet";
        String dict = "{\"(Lorem\": \"anonymized\"}";

        Assertions.assertThatThrownBy(() -> anonymizerService.handleAnonymization(content, dict))
                .isInstanceOf(PatternSyntaxException.class)
                .hasMessageContaining("Invalid dictionary key, can't compile regex");
    }


    @Test
    void should_throw_exception_when_invalid_json() {
        String content = "Lorem ipsum dolor sit amet";
        String dict = "{\"(Lorem\": \"anonymized\"";

        Assertions.assertThatThrownBy(() -> anonymizerService.handleAnonymization(content, dict))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Expected");
    }


    @Test
    void should_throw_exception_when_empty_dict() {
        String content = "Lorem ipsum dolor sit amet";
        String dict = "{}";

        Assertions.assertThatThrownBy(() -> anonymizerService.handleAnonymization(content, dict))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Expected non empty content and dictionary");
    }


    @Test
    void should_throw_exception_when_empty_content() {
        String content = "";
        String dict = "{\"(Lorem\": \"anonymized\"}";

        Assertions.assertThatThrownBy(() -> anonymizerService.handleAnonymization(content, dict))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Expected non empty content and dictionary");
    }








}
