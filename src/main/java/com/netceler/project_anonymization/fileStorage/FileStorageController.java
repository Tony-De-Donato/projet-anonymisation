package com.netceler.project_anonymization.fileStorage;

import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.*;


@RestController
public class FileStorageController {

    private final FileStorageProperties fileStorageProperties;
    private final FileStorageService fileStorageService;
    private final Path defaultLocation;
    private final Path toAnonymizeLocation;
    private final Path anonymizedLocation;


    public FileStorageController(final FileStorageService fileStorageService, final FileStorageProperties fileStorageProperties) {
        this.fileStorageService = fileStorageService;
        this.fileStorageProperties = fileStorageProperties;
        this.defaultLocation = Paths.get(fileStorageProperties.getDefaultLocation());
        this.toAnonymizeLocation = Paths.get(fileStorageProperties.getToAnonymizeLocation());
        this.anonymizedLocation = Paths.get(fileStorageProperties.getAnonymizedLocation());
    }


    @GetMapping(value="/getFile/{fileName}", produces = "application/json")
    public ResponseEntity<String> getFile(@PathVariable String fileName) throws BadRequestException {
        String file = fileStorageService.loadAsJsonString(fileName, this.defaultLocation);
        return ResponseEntity.ok()
                .body(file);
    }


    @GetMapping(value="/deleteFile/{fileName}", produces = "application/json")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) throws BadRequestException {
        fileStorageService.deleteFileIfExists(fileName, this.defaultLocation);
        if (Files.exists(this.defaultLocation.resolve(fileName))) {
            throw new BadRequestException("File could not be deleted");
        }
        else {
            return ResponseEntity.ok()
                    .body("File successfully deleted");
        }
    }


    @PostMapping(value="/upload/")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws BadRequestException {
        fileStorageService.storeMultipartFile(file, this.defaultLocation);
        return ResponseEntity.ok()
                .body("File successfully uploaded");
    }




}
