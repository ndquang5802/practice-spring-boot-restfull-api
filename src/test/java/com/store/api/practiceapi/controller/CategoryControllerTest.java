package com.store.api.practiceapi.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.store.api.practiceapi.model.Category;
import com.store.api.practiceapi.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class CategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    private String jwtToken;

    private List<Category> lstCategories;

    @BeforeEach
    public void setup() throws Exception {
        // Simulate a login request to obtain a valid JWT token
        String loginRequestBody = "{\"username\":\"trannq\", \"password\":\"123456\"}";
        String loginResponse = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestBody))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        // Mock the behavior of the service
        lstCategories = Arrays.asList(
                new Category(1L, "Record 1"),
                new Category(2L, "Record 2"),
                new Category(3L, "Record 3")
        );
        // Extract the JWT token from the login response
        jwtToken = extractToken(loginResponse);
    }

    private String extractToken(String loginResponse) {
        // Extract the token from the login response JSON
        // Adapt this method based on your specific token format and structure
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode responseJson = mapper.readTree(loginResponse);
            return responseJson.get("access_token").asText();
        } catch (Exception e) {
            // Handle the exception appropriately
            return null;
        }
    }

    @Test
    @WithMockUser
    public void testCreateRecord() throws Exception {
        // Mock the behavior of the service
        Category newRecord = Category.builder()
                .id(4L)
                .name("Test Record 4")
                .build();
//        lstCategories.add(newRecord);
        when(categoryService.saveCategory(Mockito.any(Category.class))).thenReturn(newRecord);

        // Send a POST request to create a new record with JWT authentication
        mockMvc.perform(MockMvcRequestBuilders.post("/api/management/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
                        .content("{\"name\": \"Test Record 4\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test Record 4"));
    }

    @Test
    public void testGetAllRecords() throws Exception {
        // Mock the behavior of the service
        when(categoryService.findAllCategories()).thenReturn(lstCategories);

        // Send a GET request to retrieve all records
        mockMvc.perform(MockMvcRequestBuilders.get("/api/management/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(lstCategories.size()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Record 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("Record 2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].id").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].name").value("Record 3"));
    }
}
