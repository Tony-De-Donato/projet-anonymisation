package com.netceler.project_anonymization.anonymizer;

import com.netceler.project_anonymization.dictionary.Dictionary;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.regex.PatternSyntaxException;

@SpringBootTest
class AnonymizerServiceTest {

    @Autowired
    AnonymizerService anonymizerService;

    @Test
    void should_anonymize_string() {
        String content = "lorem ipsum dolor sit amet";
        Dictionary dict = new Dictionary("loremToIpsum", "lorem", "ipsum");
        String result = anonymizerService.handleAnonymization(content, List.of(dict));

        Assertions.assertThat(result).isEqualTo("ipsum ipsum dolor sit amet");

    }

    @Test
    void should_replace_ip_address() {
        String content = "Lorem ipsum dolor sit amet, ip: 192.168.1.1";
        Dictionary dict = new Dictionary("ip", "(?<=\\s|^)(?:[0-9]{1,3}\\.){3}[0-9]{1,3}(?=\\s|$)",
                "xx.xx.xx.xx");
        String result = anonymizerService.handleAnonymization(content, List.of(dict));

        Assertions.assertThat(result).isEqualTo("Lorem ipsum dolor sit amet, ip: xx.xx.xx.xx");
    }

    @Test
    void should_anonymize_email() {
        String content = "Lorem ipsum dolor sit amet, email: anonymize@meee.com";
        Dictionary dict = new Dictionary("email",
                "(?<=\\s|^)[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}(?=\\s|$)", "username@domain.com");
        String result = anonymizerService.handleAnonymization(content, List.of(dict));

        Assertions.assertThat(result).isEqualTo("Lorem ipsum dolor sit amet, email: username@domain.com");
    }

    @Test
    void should_anonymize_multiple_regex() {
        String content = "lorem ipsum dolor sit amet, ip: 192.168.1.1 and email: anonymize@meee.com ";
        Dictionary dict1 = new Dictionary("email",
                "(?<=\\s|^)[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}(?=\\s|$)", "username@domain.com");
        Dictionary dict2 = new Dictionary("ip", "(?<=\\s|^)(?:[0-9]{1,3}\\.){3}[0-9]{1,3}(?=\\s|$)",
                "xx.xx.xx.xx");
        Dictionary dict3 = new Dictionary("loremToIpsum", "lorem", "ipsum");
        List<Dictionary> dictList = List.of(dict1, dict2, dict3);
        String result = anonymizerService.handleAnonymization(content, dictList);

        Assertions.assertThat(result)
                .isEqualTo("ipsum ipsum dolor sit amet, ip: xx.xx.xx.xx and email: username@domain.com ");
    }

    @Test
    void should_throw_exception_when_invalid_regex() {
        String content = "Lorem ipsum dolor sit amet";
        Dictionary dict = new Dictionary("email", "(lorem", "lorem");

        Assertions.assertThatThrownBy(() -> anonymizerService.handleAnonymization(content, List.of(dict)))
                .isInstanceOf(PatternSyntaxException.class)
                .hasMessageContaining("Invalid dictionary key, can't compile regex");
    }

    @Test
    void should_throw_exception_when_empty_dict() {
        String content = "Lorem ipsum dolor sit amet";
        List<Dictionary> dict = List.of();

        Assertions.assertThatThrownBy(() -> anonymizerService.handleAnonymization(content, dict))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Expected non empty content and dictionary");
    }

    @Test
    void should_throw_exception_when_empty_content() {
        String content = "";
        Dictionary dict = new Dictionary("loremToIpsum", "lorem", "ipsum");

        Assertions.assertThatThrownBy(() -> anonymizerService.handleAnonymization(content, List.of(dict)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Expected non empty content and dictionary");
    }

}
