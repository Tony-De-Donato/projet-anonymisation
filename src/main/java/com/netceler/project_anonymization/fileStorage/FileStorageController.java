package com.netceler.project_anonymization.fileStorage;

import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileStorageController {

    private final FileStorageService fileStorageService;

    public FileStorageController(final FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping(value = "/anonymize/")
    public ResponseEntity<String> anonymize(@RequestParam("file") MultipartFile file,
            @RequestParam("dictionary") MultipartFile dictionary) throws BadRequestException {
        return ResponseEntity.ok().body(fileStorageService.anonymizeFile(file, dictionary).toString());
    }

    @GetMapping(value = "/getDictFile/{filename}")
    public ResponseEntity<String> getDict(@PathVariable String filename) throws BadRequestException {
        return ResponseEntity.ok().body(fileStorageService.getDictFile(filename).toString());
    }

}
