package com.netceler.project_anonymization.fileStorage;

import org.json.JSONObject;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class FileStorageService {

    private final FileStorageProperties fileStorageProperties;

    private final Path defaultStorage;
    private final Path toAnonymizeStorage;
    private final Path anonymizedStorage;

    FileStorageService(final FileStorageProperties fileStorageProperties) throws BadRequestException {
        this.fileStorageProperties = fileStorageProperties;
        if (fileStorageProperties.getDefaultLocation().isBlank() || fileStorageProperties.getToAnonymizeLocation().isBlank() || fileStorageProperties.getAnonymizedLocation().isBlank()) {
            throw new BadRequestException("Storage locations are not set");
        }

        this.defaultStorage = Paths.get(fileStorageProperties.getDefaultLocation());
        this.toAnonymizeStorage = Paths.get(fileStorageProperties.getToAnonymizeLocation());
        this.anonymizedStorage = Paths.get(fileStorageProperties.getAnonymizedLocation());
        try {
            Files.createDirectories(defaultStorage);
            Files.createDirectories(toAnonymizeStorage);
            Files.createDirectories(anonymizedStorage);
        }
        catch (IOException e) {
            throw new BadRequestException("Could not initialize storage", e);
        }
    }





//    FILE STORAGE METHODS

    public void storeMultipartFile(MultipartFile file, Path path) throws BadRequestException {
        try {
            if (file.isEmpty()) {
                throw new BadRequestException("Failed to store empty file.");
            }
            Path destinationFile = path.resolve(
                            Paths.get(Objects.requireNonNull(file.getOriginalFilename())))
                    .normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(path.toAbsolutePath())) {
                throw new BadRequestException(
                        "Cannot store file outside current directory.");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile,
                        StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e) {
            throw new BadRequestException("Failed to store file.", e);
        }
    }

    public void storeFromJson(JSONObject file, Path path) throws BadRequestException {
        try {
            String filename = file.getString("filename");
            String content = file.getString("content");
            Path destinationFile = path.resolve(filename)
                    .normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(path.toAbsolutePath())) {
                throw new BadRequestException(
                        "Cannot store file outside current directory.");
            }
            Files.write(destinationFile, content.getBytes());
        }
        catch (IOException e) {
            throw new BadRequestException("Failed to store file.", e);
        }
    }





//    LOAD FROM STORAGE METHODS

    public Path load(String filename, Path path) throws BadRequestException {
        try {
            return path.resolve(filename);
        }
        catch (InvalidPathException e) {
            throw new BadRequestException("Can't find file :"+filename);
        }
    }

    public String loadAsJsonString(String fileName, Path path) throws BadRequestException {
        try {
            Path file = load(fileName, path);
            JSONObject jsonFile = new JSONObject();
            String content = new String(Files.readAllBytes(file));
            jsonFile.put("filename", file.getFileName());
            jsonFile.put("content", content);
            return jsonFile.toString();

        } catch (IOException e) {
            throw new BadRequestException("Could not read file: "+ fileName);
        }
    }

    public Stream<Path> loadAllFiles(Path path) throws BadRequestException {
        try {
            return Files.walk(path, 1)
                    .filter(path1 -> !path1.equals(path))
                    .map(path::relativize);
        }
        catch (IOException e) {
            throw new BadRequestException("Failed to read stored files in the directory: "+path.toString());
        }
    }

    public String loadFileContent(String fileName, Path path) throws BadRequestException {
        try {
            Path file = load(fileName, path);
            return new String(Files.readAllBytes(file));
        } catch (IOException e) {
            throw new BadRequestException("Could not read the content of the file: "+ fileName+" in the directory: "+path.toString());
        }
    }





//    DELETE METHODS

    public void clearDir(Path path) {
        FileSystemUtils.deleteRecursively(path.toFile());
    }

    public void deleteFileIfExists(String filename, Path path) throws BadRequestException {
        try {
            Files.deleteIfExists(path.resolve(filename));
        } catch (IOException e) {
            throw new BadRequestException("Failed to delete file", e);
        }
    }




}
