package com.netceler.project_anonymization.fileStorage;

import org.json.JSONObject;
import org.springframework.core.io.Resource;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
public class FileStorageController {

    private final FileStorageService fileStorageService;



    public FileStorageController(final FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping(value="/anonymize/")
    public ResponseEntity<String> anonymize(@RequestParam("file") MultipartFile file, @RequestParam("dictionary") MultipartFile dictionary) throws BadRequestException {
        return ResponseEntity.ok()
                .body(fileStorageService.anonymizeFile(file, dictionary).toString());
    }

    @GetMapping(value="/getDict/{filename}")
    public ResponseEntity<String> getDict(@PathVariable String filename) throws BadRequestException {
        return ResponseEntity.ok()
                .body(fileStorageService.getDictFile(filename).toString());
    }




}
