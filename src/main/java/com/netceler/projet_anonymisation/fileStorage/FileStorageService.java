package com.netceler.projet_anonymisation.fileStorage;

import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    private final FileStorageProperties fileStorageProperties;

    private final Path rootLocation;

    FileStorageService(final FileStorageProperties fileStorageProperties) throws BadRequestException {
        this.fileStorageProperties = fileStorageProperties;
        if (fileStorageProperties.getLocation().isBlank()){
            throw new BadRequestException("File upload location can not be empty");
        }
        this.rootLocation = Paths.get(fileStorageProperties.getLocation());
    }


    public void store(MultipartFile file) throws BadRequestException {
        try {
            if (file.isEmpty()) {
                throw new BadRequestException("Failed to store empty file.");
            }
            Path destinationFile = this.rootLocation.resolve(
                            Paths.get(file.getOriginalFilename()))
                    .normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
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


}
