package com.netceler.project_anonymization.dictionary;

import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DictionaryController {

    private final DictionaryService dictionaryService;

    public DictionaryController(final DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    @GetMapping(value = "/getDictByRuleName/{name}/{accurate}")
    public ResponseEntity<String> getDictByName(@PathVariable String name, @PathVariable Boolean accurate)
            throws BadRequestException {
        return ResponseEntity.ok()
                .body(dictionaryService.dictListToJsonString(
                        dictionaryService.getDictionaryByName(name, accurate)));
    }

    @GetMapping(value = "/getDictByFileName/{filename}")
    public ResponseEntity<String> getDictByFileName(@PathVariable String filename)
            throws BadRequestException {
        return ResponseEntity.ok()
                .body(dictionaryService.dictListToJsonString(
                        dictionaryService.getDictionaryByFileName(filename)));
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
