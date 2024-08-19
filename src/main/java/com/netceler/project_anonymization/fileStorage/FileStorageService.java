package com.netceler.project_anonymization.fileStorage;

import com.netceler.project_anonymization.anonymizer.AnonymizerService;
import com.netceler.project_anonymization.anonymizer.exceptions.AnonymizerServiceException;
import com.netceler.project_anonymization.dictionary.Dictionary;
import com.netceler.project_anonymization.dictionary.DictionaryService;
import com.netceler.project_anonymization.dictionary.exceptions.DictionaryServiceException;
import com.netceler.project_anonymization.fileStorage.exceptions.FileNotFoundException;
import com.netceler.project_anonymization.fileStorage.exceptions.FileStorageException;
import com.netceler.project_anonymization.fileStorage.exceptions.InvalidFileException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class FileStorageService {

    private final AnonymizerService anonymizerService;

    private final DictionaryService dictionaryService;

    private final Path anonymizedStorage;

    public FileStorageService(final FileStorageProperties fileStorageProperties,
            final AnonymizerService anonymizerService, final DictionaryService dictionaryService)
            throws FileStorageException {
        this.anonymizerService = anonymizerService;
        this.dictionaryService = dictionaryService;
        this.anonymizedStorage = Paths.get(fileStorageProperties.getAnonymizedLocation());
        initializeStorage(List.of(anonymizedStorage));
    }

    void initializeStorage(final List<Path> paths) throws FileStorageException {
        for (final Path path : paths) {
            try {
                Files.createDirectories(path);
            } catch (final IOException e) {
                throw new FileStorageException("Could not initialize storage", e);
            }
        }
    }

    public void storeFromFileProperties(final String filename, String content, final Path path)
            throws FileStorageException {
        try {
            final Path destinationFile = path.resolve(filename).normalize().toAbsolutePath();
            assertNotOutsideStorage(path, destinationFile);
            final BufferedWriter writer = Files.newBufferedWriter(destinationFile);
            writer.write(content);
            writer.close();
        } catch (final IOException e) {
            throw new FileStorageException("Failed to store file.", e);
        }
    }

    public void assertNotOutsideStorage(final Path path, final Path destinationFile)
            throws InvalidFileException {
        if (!destinationFile.getParent().equals(path.toAbsolutePath())) {
            throw new InvalidFileException("Cannot store file outside current directory.");
        }
    }

    public Path load(final String filename, final Path path) throws FileNotFoundException {
        try {
            return path.resolve(filename);
        } catch (final InvalidPathException e) {
            throw new FileNotFoundException("Can't find file: " + filename, e);
        }
    }

    public String loadFileContent(final String fileName, final Path path) throws FileStorageException {
        if (fileName.contains("..")) {
            throw new InvalidFileException("Cannot read file with relative path outside current directory");
        }
        try {
            final Path file = load(fileName, path);
            return new String(Files.readAllBytes(file));
        } catch (final IOException e) {
            throw new FileStorageException("Could not read the content of the file: " + fileName, e);
        }
    }

    public String readContentFromMultipartFile(MultipartFile file) throws FileStorageException {
        try {
            return new String(file.getInputStream().readAllBytes());
        } catch (Exception e) {
            throw new FileStorageException("Failed to read content from file: " + e.getMessage(), e);
        }
    }

    public String getDictFile(final String fileName) throws FileStorageException {
        if (!fileName.contains("_dict")) {
            throw new InvalidFileException("This file is not a dictionary");
        }
        try {
            return loadFileContent(fileName, anonymizedStorage);
        } catch (final Exception e) {
            throw new FileStorageException("Could not read the content of the file: " + fileName, e);
        }
    }

    public String addStringBeforeExtension(final String filename, final String string)
            throws InvalidFileException {
        try {
            final String[] parts = filename.split("\\.");
            return parts[0] + string + "." + parts[1];
        } catch (final Exception e) {
            throw new InvalidFileException("Failed to modify file name", e);
        }
    }

    public String modifyExtension(final String filename, final String extensionWithDot)
            throws InvalidFileException {
        try {
            final String[] parts = filename.split("\\.");
            return parts[parts.length - 2] + extensionWithDot;
        } catch (final Exception e) {
            throw new InvalidFileException("Failed to modify file extension", e);
        }
    }

    public JSONObject anonymizeFile(final MultipartFile file, final MultipartFile conversionDictionary)
            throws FileStorageException, DictionaryServiceException, AnonymizerServiceException {
        final String fileName = file.getOriginalFilename();
        final String fileContent = readContentFromMultipartFile(file);
        final String dictContent = readContentFromMultipartFile(conversionDictionary);
        final List<Dictionary> dictList = dictionaryService.jsonStringToDictList(dictContent);
        final String anonymizedContent = anonymizerService.handleAnonymization(fileContent, dictList);
        final String newFileName = addStringBeforeExtension(fileName, "_anonymized");
        final String newDictName = modifyExtension(
                addStringBeforeExtension(file.getOriginalFilename(), "_dict"), ".json");
        storeFromFileProperties(newFileName, anonymizedContent, anonymizedStorage);
        storeFromFileProperties(newDictName, dictContent, anonymizedStorage);
        dictionaryService.recordFromJsonFileOrUpdateExisting(fileName, dictContent);
        return new JSONObject().put("fileName", newFileName)
                .put("dict", newDictName)
                .put("content", anonymizedContent);
    }
}
