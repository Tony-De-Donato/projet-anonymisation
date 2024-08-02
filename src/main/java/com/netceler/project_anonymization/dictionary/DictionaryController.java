package com.netceler.project_anonymization.dictionary;

import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> getDictByName(@PathVariable final String name,
            @PathVariable final Boolean accurate) throws BadRequestException {
        return ResponseEntity.ok()
                .body(dictionaryService.dictListToJsonString(
                        dictionaryService.getDictionaryByName(name, accurate)));
    }

    //TODO use primitif type when you can

    @GetMapping(value = "/getDictByFileName/{filename}")
    public List<Dictionary> getDictByFileName(@PathVariable final String filename)
            throws BadRequestException {
        return dictionaryService.getDictionaryByFileName(filename);

        //TODO DOn't use Json object for stringify your list of Dictionary
    }

    @GetMapping(value = "/getAllDefaultDict")
    public ResponseEntity<String> getDefaultDict() {
        return ResponseEntity.ok()
                .body(dictionaryService.dictListToJsonString(dictionaryService.getAllDefaultPatterns()));
    }

    @GetMapping(value = "/getAllDict")
    public ResponseEntity<String> getAllDict() {
        return ResponseEntity.ok()
                .body(dictionaryService.dictListToJsonString(dictionaryService.getAllDictionaries()));
    }

}
