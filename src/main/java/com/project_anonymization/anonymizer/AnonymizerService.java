package com.project_anonymization.anonymizer;

import com.project_anonymization.anonymizer.exceptions.AnonymizerServiceException;
import com.project_anonymization.anonymizer.exceptions.InvalidContentException;
import com.project_anonymization.anonymizer.exceptions.InvalidDictionaryException;
import com.project_anonymization.anonymizer.exceptions.InvalidPatternException;
import com.project_anonymization.dictionary.Dictionary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Service
public class AnonymizerService {

    public String handleAnonymization(final String content, final List<Dictionary> dict)
            throws AnonymizerServiceException {
        checkContent(content);
        checkDictionary(dict);
        return anonymiseStringWithDictList(content, dict);
    }

    public String anonymiseStringWithDictList(final String content, final List<Dictionary> dictionary)
            throws AnonymizerServiceException {
        try {
            return dictionary.stream()
                    .reduce(content, (res, dict) -> Pattern.compile(dict.regexp())
                            .matcher(res)
                            .replaceAll(dict.replacement()), (res1, res2) -> res1);
        } catch (final PatternSyntaxException e) {
            throw new InvalidPatternException("Invalid dictionary key, can't compile regex", e.getPattern(),
                    e.getIndex());
        }
    }

    public void checkContent(final String content) throws InvalidContentException {
        if (content == null || content.isBlank()) {
            throw new InvalidContentException("Expected non empty content");
        }
    }

    public void checkDictionary(final List<Dictionary> dictionary) throws InvalidDictionaryException {
        if (dictionary == null || dictionary.isEmpty() || dictionary.stream()
                .anyMatch(
                        dict -> dict.name() == null || dict.regexp() == null || dict.replacement() == null)) {
            throw new InvalidDictionaryException("Expected non empty dictionary");
        }
    }
}