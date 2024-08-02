package com.netceler.project_anonymization.dictionary;

import com.netceler.project_anonymization.anonymizer.AnonymizerService;
import org.apache.coyote.BadRequestException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class DictionaryService {

    private final DictionaryRepository dictionaryRepository;

    private final AnonymizerService anonymizerService;

    public DictionaryService(final DictionaryRepository dictionaryRepository,
            final AnonymizerService anonymizerService) {
        this.dictionaryRepository = dictionaryRepository;
        this.anonymizerService = anonymizerService;
    }

    public List<Dictionary> getAllDefaultPatterns() {
        try {
            final List<DictionaryEntity> dictionaryEntities = dictionaryRepository.findDefaultPatterns();
            return dictEntityListToDictList(dictionaryEntities);
        } catch (final Exception e) {
            throw new RuntimeException("Error retrieving default patterns: " + e.getMessage(), e);
        }

        //TODO Throw exception smartly don't catch any exception, and don't throw Runtime Exception
    }

    public List<Dictionary> getDictionaryByName(final String name, final boolean accurate)
            throws BadRequestException {
        try {
            return dictEntityListToDictList(accurate
                    ? dictionaryRepository.findByName(name)
                    : dictionaryRepository.findByNameLike(name));
        } catch (final Exception e) {
            throw new BadRequestException("Error retrieving dictionaries: " + e.getMessage());
        }
    }

    public List<Dictionary> getAllDictionaries() {
        try {
            final List<DictionaryEntity> dictionaryEntities = dictionaryRepository.findAll();
            return dictEntityListToDictList(dictionaryEntities);
        } catch (final Exception e) {
            throw new RuntimeException("Error retrieving dictionaries: " + e.getMessage(), e);
        }
    }

    public List<Dictionary> getDictionaryByFileName(final String dictFileName) {
        try {
            final List<DictionaryEntity> dictionaryEntities = dictionaryRepository.findByDictFileName(
                    dictFileName);
            final List<Dictionary> dictionaries = new ArrayList<>();
            for (final DictionaryEntity entity : dictionaryEntities) {
                dictionaries.add(
                        new Dictionary(entity.getName(), entity.getRegexp(), entity.getReplacement()));
            }
            return dictionaries;
        } catch (final Exception e) {
            throw new RuntimeException("Error retrieving dictionaries: " + e.getMessage(), e);
        }
    }

    public List<Dictionary> dictEntityListToDictList(final List<DictionaryEntity> dictionaryEntities) {
        return dictionaryEntities.stream().map(Dictionary::from).toList();
    }

    public String dictListToJsonString(final List<Dictionary> dictionaryList) {
        final JSONObject jsonObject = new JSONObject();
        int index = 1;
        for (final Dictionary dict : dictionaryList) {
            final JSONObject dictObject = new JSONObject();
            dictObject.put("name", dict.name());
            dictObject.put("regexp", dict.regexp());
            dictObject.put("replacement", dict.replacement());
            jsonObject.put(String.valueOf(index), dictObject);
            index++;
        }
        return jsonObject.toString();
    }

    public List<Dictionary> jsonStringToDictList(final String content) {
        //TODO use objectMapper --> in your context

        try {
            final JSONObject jsonObject = anonymizerService.stringToJson(content);
            final List<Dictionary> dictionaryList = new ArrayList<>();
            for (final Iterator<String> iter = jsonObject.keys(); iter.hasNext(); ) {
                final String key = iter.next();
                final JSONObject value = jsonObject.getJSONObject(key);
                dictionaryList.add(new Dictionary(value.getString("name"), value.getString("regexp"),
                        value.getString("replacement")));
            }
            return dictionaryList;
        } catch (final Exception e) {
            throw new RuntimeException("Failed to get dictionary list from JSON :" + e.getMessage(), e);
        }
    }

    public void recordJsonFileOrUpdateExisting(final String filename, final String content) {
        try {
            final List<Dictionary> dictionaryList = jsonStringToDictList(content);
            final List<DictionaryEntity> dictionaryEntities = new ArrayList<>();
            dictionaryList.forEach(dict -> {
                final List<DictionaryEntity> dictionaryEntity = dictionaryRepository.findByDictFileNameAndName(
                        filename, dict.name());
                if (dictionaryEntity.isEmpty()) {
                    final DictionaryEntity newEntity = new DictionaryEntity();
                    newEntity.setName(dict.name());
                    newEntity.setRegexp(dict.regexp());
                    newEntity.setReplacement(dict.replacement());
                    newEntity.setDefaultPattern(false);
                    newEntity.setDictFileName(filename);
                    dictionaryEntities.add(newEntity);
                } else {
                    final DictionaryEntity entity = dictionaryEntity.getFirst();
                    entity.setRegexp(dict.regexp());
                    entity.setReplacement(dict.replacement());
                    dictionaryEntities.add(entity);
                }
                // TODO create another function for this long function
            });

            dictionaryRepository.saveAll(dictionaryEntities);
        } catch (final Exception e) {
            throw new RuntimeException("Failed to record JSON file: " + e.getMessage(), e);
        }
    }

}
