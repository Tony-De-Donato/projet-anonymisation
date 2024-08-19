package com.netceler.project_anonymization.dictionary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netceler.project_anonymization.dictionary.exceptions.DictionaryNotFoundException;
import com.netceler.project_anonymization.dictionary.exceptions.DictionaryServiceException;
import com.netceler.project_anonymization.dictionary.exceptions.InvalidDictionaryException;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;

@Service
public class DictionaryService {

    private final DictionaryRepository dictionaryRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public DictionaryService(final DictionaryRepository dictionaryRepository) {
        this.dictionaryRepository = dictionaryRepository;
    }

    public List<Dictionary> getAllDefaultPatterns() throws DictionaryServiceException {
        try {
            return dictEntityListToDictList(dictionaryRepository.findDefaultPatterns());
        } catch (final Exception e) {
            throw new DictionaryServiceException("Error retrieving default patterns: " + e.getMessage(), e);
        }
    }

    public List<Dictionary> getDictionaryByName(final String name, final boolean accurate)
            throws DictionaryServiceException {
        try {
            return dictEntityListToDictList(accurate
                    ? dictionaryRepository.findByName(name)
                    : dictionaryRepository.findByNameLike(name));
        } catch (final Exception e) {
            throw new DictionaryNotFoundException("Error retrieving dictionaries: " + e.getMessage(), e);
        }
    }

    public List<Dictionary> getAllDictionaries() throws DictionaryServiceException {
        try {
            return dictEntityListToDictList(dictionaryRepository.findAllUnique());
        } catch (final Exception e) {
            throw new DictionaryServiceException("Error retrieving dictionaries: " + e.getMessage(), e);
        }
    }

    public List<Dictionary> getDictionaryByFileName(final String dictFileName, final boolean accurate)
            throws DictionaryServiceException {
        try {
            return dictEntityListToDictList(accurate
                    ? dictionaryRepository.findByDictFileName(dictFileName)
                    : dictionaryRepository.findByDictFileNameLike(dictFileName));
        } catch (final Exception e) {
            throw new DictionaryNotFoundException("Error retrieving dictionaries: " + e.getMessage(), e);
        }
    }

    public List<Dictionary> dictEntityListToDictList(final List<DictionaryEntity> dictionaryEntities) {
        return dictionaryEntities.stream().map(Dictionary::from).toList();
    }

    public DictionaryEntity dictToDictEntity(final Dictionary dict, String filename, String hash) {
        final DictionaryEntity entity = new DictionaryEntity();
        entity.setName(dict.name());
        entity.setRegexp(dict.regexp());
        entity.setReplacement(dict.replacement());
        entity.setDefaultPattern(false);
        entity.setDictFileName(filename);
        entity.setUniqueness(hash);
        return entity;
    }

    public List<Dictionary> jsonStringToDictList(final String content) throws DictionaryServiceException {
        try {
            return Arrays.asList(objectMapper.readValue(content, Dictionary[].class));
        } catch (final Exception e) {
            throw new InvalidDictionaryException(
                    "Failed to get dictionary list from JSON: " + content + e.getMessage(), e);
        }
    }

    public void dictListVerificationAndRecord(final List<Dictionary> dictionaryList, String filename)
            throws DictionaryServiceException {
        try {
            dictionaryList.stream()
                    .map(dict -> {
                        String hash = String.valueOf(
                                (dict.name() + dict.regexp() + dict.replacement()).hashCode());
                        return new AbstractMap.SimpleEntry<>(dict, hash);
                    })
                    .filter(entry -> dictionaryRepository.findByUniquenessAndFilename(entry.getValue(),
                            filename).isEmpty())
                    .forEach(entry -> dictionaryRepository.save(
                            dictToDictEntity(entry.getKey(), filename, entry.getValue())));
        } catch (final Exception e) {
            throw new DictionaryServiceException("Failed to verify dictionary list: " + e.getMessage(), e);
        }
    }

    public void recordFromJsonFileOrUpdateExisting(final String filename, final String content)
            throws DictionaryServiceException {
        try {
            dictListVerificationAndRecord(jsonStringToDictList(content), filename);
        } catch (final Exception e) {
            throw new DictionaryServiceException("Failed to record JSON file: " + e.getMessage(), e);
        }
    }
}