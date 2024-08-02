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

    public DictionaryService(DictionaryRepository dictionaryRepository, AnonymizerService anonymizerService) {
        this.dictionaryRepository = dictionaryRepository;
        this.anonymizerService = anonymizerService;
    }



    public List<Dictionary> getAllDefaultPatterns() {
        try {
            List<DictionaryEntity> dictionaryEntities = dictionaryRepository.findDefaultPatterns();
            return dictEntityListToDictList(dictionaryEntities);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving default patterns: " + e.getMessage(), e);
        }
    }

    public List<Dictionary> getDictionaryByName(String name, Boolean accurate) throws BadRequestException {
        List<DictionaryEntity> dictionaryEntities;
        try {
            if (accurate) {
                dictionaryEntities = dictionaryRepository.findByName(name);
            } else {
                dictionaryEntities = dictionaryRepository.findByNameLike(name);
            }
            return dictEntityListToDictList(dictionaryEntities);
        } catch (Exception e) {
            throw new BadRequestException("Error retrieving dictionaries: " + e.getMessage());
        }
    }

    public List<Dictionary> getAllDictionaries() {
        try {
            List<DictionaryEntity> dictionaryEntities = dictionaryRepository.findAllRecords();
            return dictEntityListToDictList(dictionaryEntities);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving dictionaries: " + e.getMessage(), e);
        }
    }

    public List<Dictionary> getDictionaryByFileName(String dictFileName) {
        try {
            List<DictionaryEntity> dictionaryEntities = dictionaryRepository.findByDictFileName(dictFileName);
            List<Dictionary> dictionaries = new ArrayList<>();
            for (DictionaryEntity entity : dictionaryEntities) {
                dictionaries.add(new Dictionary(entity.getName(), entity.getRegexp(), entity.getReplacement()));
            }
            return dictionaries;
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving dictionaries: " + e.getMessage(), e);
        }
    }


    public List<Dictionary> dictEntityListToDictList(List<DictionaryEntity> dictionaryEntities) {
        List<Dictionary> dictionaries = new ArrayList<>();
        for (DictionaryEntity entity : dictionaryEntities) {
            dictionaries.add(new Dictionary(entity.getName(), entity.getRegexp(), entity.getReplacement()));
        }
        return dictionaries;
    }

    public String dictListToJsonString(List<Dictionary> dictionaryList) {
        JSONObject jsonObject = new JSONObject();
        int index = 1;
        for (Dictionary dict : dictionaryList) {
            JSONObject dictObject = new JSONObject();
            dictObject.put("name", dict.name());
            dictObject.put("regexp", dict.regexp());
            dictObject.put("replacement", dict.replacement());
            jsonObject.put(String.valueOf(index), dictObject);
            index++;
        }
        return jsonObject.toString();
    }

    public List<Dictionary> jsonStringToDictList(String content) {
        try {
            JSONObject jsonObject = anonymizerService.stringToJson(content);
            List<Dictionary> dictionaryList = new ArrayList<>();
            for (Iterator<String> iter = jsonObject.keys(); iter.hasNext(); ) {
                String key = iter.next();
                JSONObject value = jsonObject.getJSONObject(key);
                dictionaryList.add(new Dictionary(value.getString("name"), value.getString("regexp"), value.getString("replacement")));
            }
            return dictionaryList;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get dictionary list from JSON :"+e.getMessage(), e);
        }
    }

    public void recordJsonFileOrUpdateExisting(String filename, String content) {
        try {
            List<Dictionary> dictionaryList = jsonStringToDictList(content);
            List<DictionaryEntity> dictionaryEntities = new ArrayList<>();
            for (Dictionary dict : dictionaryList) {
                List<DictionaryEntity> dictionaryEntity = dictionaryRepository.findByDictFileNameAndName(filename, dict.name());
                if (dictionaryEntity.isEmpty()) {
                    DictionaryEntity newEntity = new DictionaryEntity();
                    newEntity.setName(dict.name());
                    newEntity.setRegexp(dict.regexp());
                    newEntity.setReplacement(dict.replacement());
                    newEntity.setDefaultPattern(false);
                    newEntity.setDictFileName(filename);
                    dictionaryEntities.add(newEntity);
                } else {
                    DictionaryEntity entity = dictionaryEntity.getFirst();
                    entity.setRegexp(dict.regexp());
                    entity.setReplacement(dict.replacement());
                    dictionaryEntities.add(entity);
                }
            }
            dictionaryRepository.saveAll(dictionaryEntities);
        } catch (Exception e) {
            throw new RuntimeException("Failed to record JSON file: " + e.getMessage(), e);
        }
    }

}
