package com.project_anonymization.fileStorage;

import com.project_anonymization.anonymizer.AnonymizerService;
import com.project_anonymization.anonymizer.exceptions.AnonymizerServiceException;
import com.project_anonymization.dictionary.DictionaryService;
import com.project_anonymization.dictionary.exceptions.DictionaryServiceException;
import com.project_anonymization.fileStorage.exceptions.FileNotFoundException;
import com.project_anonymization.fileStorage.exceptions.FileStorageException;
import com.project_anonymization.fileStorage.exceptions.InvalidFileException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@Testcontainers
public class FileStorageServiceTest {

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.3");

    private final Path Storage = Paths.get("src/test/resources/fileStorage/filesAnonymized");

    @MockBean
    private AnonymizerService anonymizerService;

    @MockBean
    private DictionaryService dictionaryService;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private FileStorageProperties fileStorageProperties;

    @Autowired
    private FileStorageService fileStorageService;

    private Path anonymizedStorage;

    @BeforeEach
    void setUp() {
        anonymizedStorage = Paths.get("src/test/resources/fileStorage/filesAnonymized");
        when(fileStorageProperties.getAnonymizedLocation()).thenReturn(
                "src/test/resources/fileStorage/filesAnonymized");
    }

    @Test
    void test_initialize_storage() throws FileStorageException {
        fileStorageService.initializeStorage(List.of(anonymizedStorage));
        assertTrue(Files.exists(anonymizedStorage));
    }

    @Test
    void test_store_from_file_properties() throws FileStorageException, IOException {
        String filename = "test.txt";
        String content = "test content";
        Path path = Storage;

        fileStorageService.storeFromFileProperties(filename, content, path);

        Path destinationFile = path.resolve(filename).normalize().toAbsolutePath();
        assertTrue(Files.exists(destinationFile));
        assertEquals(content, Files.readString(destinationFile));
    }

    @Test
    void test_assert_not_outside_storage() {
        Path path = Storage;
        Path destinationFile = path.resolve("test.txt").normalize().toAbsolutePath();

        assertDoesNotThrow(() -> fileStorageService.assertNotOutsideStorage(path, destinationFile));
    }

    @Test
    void test_load() throws FileNotFoundException {
        Path path = Paths.get("anonymized");
        String filename = "test.txt";
        Path filePath = path.resolve(filename);

        Path result = fileStorageService.load(filename, path);

        assertEquals(filePath, result);
    }

    @Test
    void test_load_file_content() throws FileStorageException, IOException {
        String filename = "test.txt";
        Path path = Storage;
        Files.writeString(path.resolve(filename), "test content");

        String content = fileStorageService.loadFileContent(filename, path);

        assertEquals("test content", content);
    }

    @Test
    void test_read_content_from_multipart_file() throws FileStorageException, IOException {
        InputStream inputStream = mock(InputStream.class);
        when(multipartFile.getInputStream()).thenReturn(inputStream);
        when(inputStream.readAllBytes()).thenReturn("test content".getBytes());

        String content = fileStorageService.readContentFromMultipartFile(multipartFile);

        assertEquals("test content", content);
    }

    @Test
    void test_get_dict_file() throws FileStorageException, IOException {
        String filename = "test_dict.json";
        Files.writeString(Storage.resolve(filename), "test content");

        String content = fileStorageService.getDictFile(filename);

        assertEquals("test content", content);
    }

    @Test
    void test_add_string_before_extension() throws InvalidFileException {
        String filename = "test.txt";
        String result = fileStorageService.addStringBeforeExtension(filename, "_anonymized");

        assertEquals("test_anonymized.txt", result);
    }

    @Test
    void test_modify_extension() throws InvalidFileException {
        String filename = "test.txt";
        String result = fileStorageService.modifyExtension(filename, ".json");

        assertEquals("test.json", result);
    }

    @Test
    void test_anonymize_file()
            throws FileStorageException, IOException, DictionaryServiceException, AnonymizerServiceException {
        when(multipartFile.getOriginalFilename()).thenReturn("test.txt");
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("test content".getBytes()));
        when(dictionaryService.jsonStringToDictList(anyString())).thenReturn(List.of());
        when(anonymizerService.handleAnonymization(anyString(), anyList())).thenReturn("anonymized content");

        JSONObject result = fileStorageService.anonymizeFile(multipartFile, multipartFile);

        assertNotNull(result);
        assertTrue(result.getString("fileName").contains("test"));
        assertTrue(result.getString("fileName").contains("_anonymized"));
        assertTrue(result.getString("dict").contains("_dict.json"));
        assertEquals("anonymized content", result.getString("content"));
    }
}