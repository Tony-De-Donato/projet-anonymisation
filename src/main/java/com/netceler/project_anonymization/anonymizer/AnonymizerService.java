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


    public String handleAnonymization(String content, List<Dictionary> dict) {

        checkBothContent(content, dict);

        return anonymiseStringWithDictList(content, dict);
    }

    public String anonymiseStringWithDictList(String content, List<Dictionary> dictionary) {
        try {
            String result = content;
            for (Dictionary dict : dictionary) {
                String regex = dict.regexp();
                String replacement = dict.replacement();
                Pattern compiledRegex = Pattern.compile(regex);
                Matcher matcher = compiledRegex.matcher(result);
                result = matcher.replaceAll(replacement);
            }
            return result;
        } catch (PatternSyntaxException e) {
            throw new PatternSyntaxException("Invalid dictionary key, can't compile regex", e.getPattern(), e.getIndex());
        }
    }

    public JSONObject stringToJson(String toConvert) throws IllegalArgumentException {
        try {
            return new JSONObject(toConvert);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage() + " : " + toConvert);
        }
    }


    public void checkBothContent(String content, List<Dictionary> dictionary) {
        if (content == null || dictionary == null || content.isBlank() || dictionary.isEmpty()) {
            throw new IllegalArgumentException("Expected non empty content and dictionary");
        }
        if (dictionary.stream().anyMatch(dict -> dict.name() == null || dict.regexp() == null || dict.replacement() == null)) {
            throw new IllegalArgumentException("Expected non empty content and dictionary");
        }
    }


}
