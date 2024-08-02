package com.netceler.project_anonymization.fileStorage;

import com.netceler.project_anonymization.anonymizer.AnonymizerService;
import com.netceler.project_anonymization.dictionary.DictionaryService;
import org.apache.coyote.BadRequestException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
public class FileStorageService {

    private final AnonymizerService anonymizerService;

    private final DictionaryService dictionaryService;

    private final Path defaultStorage;

    private final Path toAnonymizeStorage;

    private final Path anonymizedStorage;

    FileStorageService(final FileStorageProperties fileStorageProperties,
            final AnonymizerService anonymizerService, final DictionaryService dictionaryService)
            throws BadRequestException {
        this.anonymizerService = anonymizerService;
        this.dictionaryService = dictionaryService;

        if (fileStorageProperties.getDefaultLocation()
                .isBlank() || fileStorageProperties.getToAnonymizeLocation()
                .isBlank() || fileStorageProperties.getAnonymizedLocation().isBlank()) {
            throw new BadRequestException("Storage locations are not set");
        }

        this.defaultStorage = Paths.get(fileStorageProperties.getDefaultLocation());
        this.toAnonymizeStorage = Paths.get(fileStorageProperties.getToAnonymizeLocation());
        this.anonymizedStorage = Paths.get(fileStorageProperties.getAnonymizedLocation());

        try {
            Files.createDirectories(defaultStorage);
            Files.createDirectories(toAnonymizeStorage);
            Files.createDirectories(anonymizedStorage);
        } catch (IOException e) {
            throw new BadRequestException("Could not initialize storage");
        }
    }

    public void storeFile(MultipartFile file, Path path) throws BadRequestException {
        try {
            if (file.isEmpty()) {
                throw new BadRequestException("Failed to store empty file");
            }
            Path destinationFile = path.resolve(Objects.requireNonNull(file.getOriginalFilename()))
                    .normalize()
                    .toAbsolutePath();
            assertNotOutsideStorage(path, destinationFile);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new BadRequestException("Failed to store file");
            }
        } catch (IOException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public void storeFromJson(JSONObject file, Path path) throws BadRequestException {
        try {
            String filename = file.getString("filename");
            String content = file.getString("content");
            Path destinationFile = path.resolve(filename).normalize().toAbsolutePath();
            assertNotOutsideStorage(path, destinationFile);
            try (BufferedWriter writer = Files.newBufferedWriter(destinationFile)) {
                writer.write(content);
            }
        } catch (IOException e) {
            throw new BadRequestException("Failed to store file.");
        }
    }

    public void assertNotOutsideStorage(Path path, Path destinationFile) throws BadRequestException {
        if (!destinationFile.getParent().equals(path.toAbsolutePath())) {
            throw new BadRequestException("Cannot store file outside current directory.");
        }
    }

    public Path load(String filename, Path path) throws BadRequestException {
        try {
            return path.resolve(filename);
        } catch (InvalidPathException e) {
            throw new BadRequestException("Can't find file :" + filename);
        }
    }

    public String loadFileContent(String fileName, Path path) throws BadRequestException {
        if (fileName.contains("..")) {
            throw new BadRequestException("Cannot read file with relative path outside current directory");
        }
        try {
            Path file = load(fileName, path);
            return new String(Files.readAllBytes(file));
        } catch (IOException e) {
            throw new BadRequestException("Could not read the content of the file: " + fileName);
        }
    }

    public JSONObject getDictFile(String fileName) throws BadRequestException {
        if (!fileName.contains("_dict")) {
            throw new BadRequestException("This file is not a dictionary");
        }
        try {
            String content = loadFileContent(fileName, anonymizedStorage);
            return new JSONObject(content);
        } catch (Exception e) {
            throw new BadRequestException("Could not read the content of the file: " + fileName);
        }

    }

    public void deleteFileIfExists(String filename, Path path) throws BadRequestException {
        try {
            Files.deleteIfExists(path.resolve(filename));
        } catch (IOException e) {
            throw new BadRequestException("Failed to delete file", e);
        }
    }

    public String addStringBeforeExtension(String filename, String string) {
        String[] parts = filename.split("\\.");
        return parts[0] + string + "." + parts[1];
    }

    public String modifyExtension(String filename, String extensionWithDot) {
        String[] parts = filename.split("\\.");
        return parts[parts.length - 2] + extensionWithDot;
    }

    public JSONObject anonymizeFile(MultipartFile file, MultipartFile conversionDictionary)
            throws BadRequestException {
        storeFile(file, toAnonymizeStorage);
        storeFile(conversionDictionary, toAnonymizeStorage);

        try {
            String fileContent = loadFileContent(Objects.requireNonNull(file.getOriginalFilename()),
                    toAnonymizeStorage);
            String dictContent = loadFileContent(
                    Objects.requireNonNull(conversionDictionary.getOriginalFilename()), toAnonymizeStorage);

            String anonymizedContent = anonymizerService.handleAnonymization(fileContent,
                    dictionaryService.jsonStringToDictList(dictContent));

            String newFileName = addStringBeforeExtension(file.getOriginalFilename(), "_anonymized");
            String newDictName = modifyExtension(
                    addStringBeforeExtension(file.getOriginalFilename(), "_dict"), ".json");

            storeFromJson(new JSONObject().put("filename", newFileName).put("content", anonymizedContent),
                    anonymizedStorage);
            storeFromJson(new JSONObject().put("filename", newDictName).put("content", dictContent),
                    anonymizedStorage);
            dictionaryService.recordJsonFileOrUpdateExisting(newDictName, dictContent);

            deleteFileIfExists(file.getOriginalFilename(), toAnonymizeStorage);
            deleteFileIfExists(conversionDictionary.getOriginalFilename(), toAnonymizeStorage);

            return new JSONObject().put("fileName", newFileName)
                    .put("dict", newDictName)
                    .put("content", anonymizedContent);

        } catch (BadRequestException e) {
            throw new BadRequestException("Failed to anonymize file");
        }

    }

}
