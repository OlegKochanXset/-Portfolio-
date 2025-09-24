package com.example.stubservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class StubControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void testValidGetRequest() throws Exception {
		mockMvc.perform(get("/app/v1/getRequest")
						.param("id", "15")
						.param("name", "Alexander"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Alexander")));
	}

	@Test
	void testInvalidId() throws Exception {
		mockMvc.perform(get("/app/v1/getRequest")
						.param("id", "5")
						.param("name", "Alex"))
				.andExpect(status().isInternalServerError());
	}
}