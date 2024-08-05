package com.netceler.project_anonymization.dictionary;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DictionaryController {

    private final DictionaryService dictionaryService;

    public DictionaryController(final DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    @GetMapping(value = "/getDictByRuleName/{name}/{accurate}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Dictionary> getDictByName(@PathVariable final String name,
            @PathVariable final boolean accurate) throws BadRequestException {
        return dictionaryService.getDictionaryByName(name, accurate);
    }

    @GetMapping(value = "/getDictByFileName/{filename}", produces = "application/json")
    public List<Dictionary> getDictByFileName(@PathVariable final String filename) {
        return dictionaryService.getDictionaryByFileName(filename);

        //TODO Don't use Json object for stringify your list of Dictionary
    }

    @GetMapping(value = "/getAllDefaultDict", produces = "application/json")
    public List<Dictionary> getDefaultDict() {
        return dictionaryService.getAllDefaultPatterns();
    }

    @GetMapping(value = "/getAllDict", produces = "application/json")
    public List<Dictionary> getAllDict() {
        return dictionaryService.getAllDictionaries();
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequestException(BadRequestException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

}
