package com.netceler.project_anonymization.anonymizer;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;



@Service
public class AnonymizerService {


    public String handleAnonymization(String content, String dict) {
        dict = replaceIllegalEscape(dict);
        JSONObject dictionary = stringToJson(dict);
        checkBothContent(content, dictionary);

        return undoIllegalEscape(anonymiseString(content, dictionary));
    }

    public String anonymiseString(String content, JSONObject dictionary) {
        try {
            for (Iterator<String> it = dictionary.keys(); it.hasNext(); ) {
                String regexKey = it.next();
                String value = dictionary.getString(regexKey);
                Pattern pattern = Pattern.compile(regexKey);
                Matcher matcher = pattern.matcher(content);
                content = matcher.replaceAll(value);
            }
            return content;
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

    public String replaceIllegalEscape(String dict) {
        if (dict.contains("\\")) {
            dict = dict.replace("\\", "\\\\");
        }
        return dict;
    }

    public String undoIllegalEscape(String dict) {
        if (dict.contains("\\\\")) {
            dict = dict.replace("\\\\", "\\");
        }
        return dict;
    }

    public void checkBothContent(String content, JSONObject dictionary) {
        if (content == null || dictionary == null || content.isBlank() || dictionary.isEmpty()) {
            throw new IllegalArgumentException("Expected non empty content and dictionary");
        }
    }


}
