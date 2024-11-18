package com.project_anonymization.dictionary;

import com.project_anonymization.dictionary.exceptions.DictionaryServiceException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DictionaryController {

    private final DictionaryService dictionaryService;

    public DictionaryController(final DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    @GetMapping(value = "/getDictByRuleName/{name}/{accurate}")
    public List<Dictionary> getDictByName(@PathVariable final String name,
            @PathVariable final boolean accurate) throws DictionaryServiceException {
        return dictionaryService.getDictionaryByName(name, accurate);
    }

    @GetMapping(value = "/getDictByFileName/{filename}/{accurate}")
    public List<Dictionary> getDictByFileName(@PathVariable final String filename,
            @PathVariable final boolean accurate) throws DictionaryServiceException {
        return dictionaryService.getDictionaryByFileName(filename, accurate);

    }

    @GetMapping(value = "/getAllDefaultDict")
    public List<Dictionary> getDefaultDict() throws DictionaryServiceException {
        return dictionaryService.getAllDefaultPatterns();
    }

    @GetMapping(value = "/getAllDict")
    public List<Dictionary> getAllDict() throws DictionaryServiceException {
        return dictionaryService.getAllDictionaries();
    }

}
