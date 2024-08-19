package com.netceler.project_anonymization.dictionary;

import com.netceler.project_anonymization.dictionary.exceptions.DictionaryServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DictionaryServiceTest {

    @Mock
    private DictionaryRepository dictionaryRepository;

    @InjectMocks
    private DictionaryService dictionaryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void test_get_all_default_patterns() throws DictionaryServiceException {
        List<DictionaryEntity> entities = List.of(
                dictionaryService.dictToDictEntity(new Dictionary("name", "regex", "replacement"),
                        "file.json", "hash"));
        when(dictionaryRepository.findDefaultPatterns()).thenReturn(entities);

        List<Dictionary> result = dictionaryService.getAllDefaultPatterns();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(dictionaryRepository, times(1)).findDefaultPatterns();
    }

    @Test
    void test_get_dictionary_by_name() throws DictionaryServiceException {
        List<DictionaryEntity> entities = List.of(
                dictionaryService.dictToDictEntity(new Dictionary("name", "regex", "replacement"),
                        "file.json", "hash"));
        when(dictionaryRepository.findByName("name")).thenReturn(entities);

        List<Dictionary> result = dictionaryService.getDictionaryByName("name", true);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(dictionaryRepository, times(1)).findByName("name");
    }

    @Test
    void test_get_all_dictionaries() throws DictionaryServiceException {
        List<DictionaryEntity> entities = List.of(
                dictionaryService.dictToDictEntity(new Dictionary("name", "regex", "replacement"),
                        "file.json", "hash"));
        when(dictionaryRepository.findAll()).thenReturn(entities);

        List<Dictionary> result = dictionaryService.getAllDictionaries();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(dictionaryRepository, times(1)).findAll();
    }

    @Test
    void test_get_dictionary_by_file_name() throws DictionaryServiceException {
        List<DictionaryEntity> entities = List.of(
                dictionaryService.dictToDictEntity(new Dictionary("name", "regex", "replacement"),
                        "file.json", "hash"));
        when(dictionaryRepository.findByDictFileName("file.json")).thenReturn(entities);

        List<Dictionary> result = dictionaryService.getDictionaryByFileName("file.json", true);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(dictionaryRepository, times(1)).findByDictFileName("file.json");
    }

    @Test
    void test_json_string_to_dict_list() throws DictionaryServiceException {
        String json = "[{\"name\":\"name\",\"regexp\":\"regex\",\"replacement\":\"replacement\"}]";
        List<Dictionary> result = dictionaryService.jsonStringToDictList(json);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void test_dict_list_verification_and_record() throws DictionaryServiceException {
        List<Dictionary> dictionaries = List.of(new Dictionary("name", "regex", "replacement"));
        when(dictionaryRepository.findByUniquenessAndFilename(anyString(), anyString())).thenReturn(
                List.of());

        dictionaryService.dictListVerificationAndRecord(dictionaries, "file.json");

        verify(dictionaryRepository, times(1)).save(any(DictionaryEntity.class));
    }

    @Test
    void test_record_from_json_file_or_update_existing() throws DictionaryServiceException {
        String json = "[{\"name\":\"name\",\"regexp\":\"regex\",\"replacement\":\"replacement\"}]";
        when(dictionaryRepository.findByUniquenessAndFilename(anyString(), anyString())).thenReturn(
                List.of());

        dictionaryService.recordFromJsonFileOrUpdateExisting("file.json", json);

        verify(dictionaryRepository, times(1)).save(any(DictionaryEntity.class));
    }
}