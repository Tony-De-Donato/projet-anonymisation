package com.netceler.project_anonymization;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.netceler.project_anonymization.fileStorage.FileStorageService;
import org.apache.coyote.BadRequestException;
import org.assertj.core.api.Assertions;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ProjectAnonymizationApplicationTests {


	@Autowired
	MockMvc mockMvc;

	@Autowired
	FileStorageService fileStorageService;

	@Autowired
	ObjectMapper objectMapper;

	@Test
	void should_return_a_file_when_get_url_is_called() throws Exception {
		final var defaultObject = new JSONObject().put("filename", "test.txt").put("content", "text for test").toString();
		mockMvc.perform(get("/getFile/test.txt"))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json"))
				.andExpect(content().json(defaultObject));
	}

	@Test
	void should_throw_exception_when_file_not_found() throws Exception {

		Assertions.assertThatThrownBy(() -> mockMvc.perform(
				get("/getFile/notfound.txt")))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Could not read file: notfound.txt");
	}

	@Test
	void should_throw_exception_when_file_cant_be_read() throws Exception {

		Assertions.assertThatThrownBy(() -> mockMvc.perform(
				get("/getFile/cantBeRead")))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Could not read file: cantBeRead");
	}

	@Test
	void should_save_uploaded_file_then_be_able_to_read_it() throws Exception {
		final var defaultObject = new JSONObject().put("filename", "test2.txt").put("content", "text for test2").toString();

		MockMultipartFile multipartFile = new MockMultipartFile("file", "test2.txt",
				"text/plain", "text for test2".getBytes());

		mockMvc.perform(multipart("/upload/").file(multipartFile))
				.andExpect(status().isOk())
				.andExpect(content().string("File successfully uploaded"));

		then(mockMvc.perform(get("/getFile/test2.txt"))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json"))
				.andExpect(content().json(defaultObject)));
	}

	@Test
	void should_delete_file_when_delete_url_is_called() throws Exception {
		MockMultipartFile multipartFile = new MockMultipartFile("file", "test2.txt",
				"text/plain", "text for test2".getBytes());

		mockMvc.perform(multipart("/upload/").file(multipartFile))
				.andExpect(status().isOk())
				.andExpect(content().string("File successfully uploaded"));
		then(mockMvc.perform(get("/deleteFile/testToDelete.txt"))
				.andExpect(status().isOk())
				.andExpect(content().string("File successfully deleted")));
	}

}
