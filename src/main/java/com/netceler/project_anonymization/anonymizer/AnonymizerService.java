package com.netceler.project_anonymization.anonymizer;

import com.netceler.project_anonymization.dictionary.Dictionary;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Service
public class AnonymizerService {

    public String handleAnonymization(final String content, final List<Dictionary> dict) {

        checkBothContent(content, dict);

        return anonymiseStringWithDictList(content, dict);
    }

    public String anonymiseStringWithDictList(final String content, final List<Dictionary> dictionary) {
        try {
            String result = content;
            for (final Dictionary dict : dictionary) {
                final String regex = dict.regexp();
                final String replacement = dict.replacement();
                final Pattern compiledRegex = Pattern.compile(regex);
                final Matcher matcher = compiledRegex.matcher(result);
                result = matcher.replaceAll(replacement);
            }
            return result;
        } catch (final PatternSyntaxException e) {
            throw new PatternSyntaxException("Invalid dictionary key, can't compile regex", e.getPattern(),
                    e.getIndex());
        }
    }

    public JSONObject stringToJson(final String toConvert) throws IllegalArgumentException {
        try {
            return new JSONObject(toConvert);
        } catch (final Exception e) {
            throw new IllegalArgumentException(e.getMessage() + " : " + toConvert);
        }
    }

    public void checkBothContent(final String content, final List<Dictionary> dictionary) {
        if (content == null || dictionary == null || content.isBlank() || dictionary.isEmpty()) {
            throw new IllegalArgumentException("Expected non empty content and dictionary");
        }
        if (dictionary.stream()
                .anyMatch(
                        dict -> dict.name() == null || dict.regexp() == null || dict.replacement() == null)) {
            throw new IllegalArgumentException("Expected non empty content and dictionary");
        }

        // TODO check content with two method separately
    }

}
