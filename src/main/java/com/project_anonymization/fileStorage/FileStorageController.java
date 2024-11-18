package com.project_anonymization.fileStorage;

import com.project_anonymization.anonymizer.exceptions.AnonymizerServiceException;
import com.project_anonymization.dictionary.exceptions.DictionaryServiceException;
import com.project_anonymization.fileStorage.exceptions.FileStorageException;
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
    public String anonymize(@RequestParam("file") final MultipartFile file,
            @RequestParam("dictionary") final MultipartFile dictionary)
            throws FileStorageException, DictionaryServiceException, AnonymizerServiceException {
        return fileStorageService.anonymizeFile(file, dictionary).toString();
    }

    @GetMapping(value = "/getDictFile/{filename}")
    public String getDict(@PathVariable final String filename) throws FileStorageException {
        return fileStorageService.getDictFile(filename);
    }

}
