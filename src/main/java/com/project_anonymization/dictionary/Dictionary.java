package com.project_anonymization.dictionary;

public record Dictionary(String name, String regexp, String replacement) {

    static Dictionary from(final DictionaryEntity dictionaryEntity) {
        return new Dictionary(dictionaryEntity.getName(), dictionaryEntity.getRegexp(),
                dictionaryEntity.getReplacement());
    }

}
