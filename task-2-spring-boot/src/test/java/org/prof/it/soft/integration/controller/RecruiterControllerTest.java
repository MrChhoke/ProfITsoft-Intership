package org.prof.it.soft.integration.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.prof.it.soft.integration.annotation.IT;
import org.prof.it.soft.repo.RecruiterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IT
@AutoConfigureMockMvc
class RecruiterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecruiterRepository recruiterRepository;

    public void setUp() {
        recruiterRepository.deleteAll();
    }

    @Test
    void saveRecruiter_shouldReturnOk_whenRequestIsValid() throws Exception {
        String request = """
                {
                      "first_name": "John",
                      "last_name": "Doe",
                      "company_name": "Google"
                }
                """;


        mockMvc.perform(post("/api/v1/recruiter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recruiter_id").exists())
                .andExpect(jsonPath("$.person_id").exists())
                .andExpect(jsonPath("$.first_name").value("John"))
                .andExpect(jsonPath("$.last_name").value("Doe"))
                .andExpect(jsonPath("$.company_name").value("Google"))
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());
    }

    @Test
    void saveRecruiter_shouldReturnOk_whenRequestWithoutCompanyName() throws Exception {
        String request = """
                {
                      "first_name": "John",
                      "last_name": "Doe"
                }
                """;

        mockMvc.perform(post("/api/v1/recruiter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recruiter_id").exists())
                .andExpect(jsonPath("$.person_id").exists())
                .andExpect(jsonPath("$.first_name").value("John"))
                .andExpect(jsonPath("$.last_name").value("Doe"))
                .andExpect(jsonPath("$.company_name").doesNotExist())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());
    }

    @Test
    void saveRecruiter_shouldReturnOk_whenRequestWithoutCompanyNameAndLastName() throws Exception {
        String request = """
                {
                      "first_name": "John"
                }
                """;

        mockMvc.perform(post("/api/v1/recruiter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recruiter_id").exists())
                .andExpect(jsonPath("$.person_id").exists())
                .andExpect(jsonPath("$.first_name").value("John"))
                .andExpect(jsonPath("$.last_name").doesNotExist())
                .andExpect(jsonPath("$.company_name").doesNotExist())
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());
    }

    @Test
    void saveRecruiter_shouldReturnBadRequest_whenRequestWithNoData() throws Exception {
        String request = """
                {
                }
                """;

        mockMvc.perform(post("/api/v1/recruiter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveRecruiter_shouldReturnBadRequest_whenRequestWithEmptyData() throws Exception {
        String request = """
                {
                      "first_name": "",
                      "last_name": "",
                      "company_name": ""
                }
                """;

        mockMvc.perform(post("/api/v1/recruiter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveRecruiter_shouldReturnBadRequest_whenRequestWithNullData() throws Exception {
        String request = """
                {
                      "first_name": null,
                      "last_name": null,
                      "company_name": null
                }
                """;

        mockMvc.perform(post("/api/v1/recruiter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveRecruiter_shouldReturnBadRequest_whenRequestWithoutFirstName() throws Exception {
        String request = """
                {
                      "last_name": "Doe",
                      "company_name": "Google"
                }
                """;

        mockMvc.perform(post("/api/v1/recruiter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateRecruiter_shouldReturnOk_whenRequestIsValid() throws Exception {
        // Given
        String request = """
                {
                      "first_name": "John",
                      "last_name": "Doe",
                      "company_name": "Google"
                }
                """;

        String responseBody = mockMvc.perform(post("/api/v1/recruiter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recruiter_id").exists())
                .andExpect(jsonPath("$.person_id").exists())
                .andExpect(jsonPath("$.first_name").value("John"))
                .andExpect(jsonPath("$.last_name").value("Doe"))
                .andExpect(jsonPath("$.company_name").value("Google"))
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var recruiterId = JsonPath.read(responseBody, "$.recruiter_id");
        var updatedAt = JsonPath.read(responseBody, "$.updated_at");
        var createdAt = JsonPath.read(responseBody, "$.updated_at");


        // When
        String updateRequest = """
                {
                      "company_name": "Microsoft"
                }
                """;

        // Then
        mockMvc.perform(put("/api/v1/recruiter/{id}", recruiterId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Recruiter updated successfully"));

        mockMvc.perform(get("/api/v1/recruiter/{id}", recruiterId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recruiter_id").value(recruiterId))
                .andExpect(jsonPath("$.person_id").exists())
                .andExpect(jsonPath("$.first_name").value("John"))
                .andExpect(jsonPath("$.last_name").value("Doe"))
                .andExpect(jsonPath("$.company_name").value("Microsoft"))
                .andExpect(jsonPath("$.created_at").value(createdAt))
                .andExpect(jsonPath("$.updated_at", org.hamcrest.Matchers.not(updatedAt)));
    }

    @Test
    void updateRecruiter_shouldReturnOk_whenPutRequestIsEmpty() throws Exception {
        // Given
        String request = """
                {
                      "first_name": "John",
                      "last_name": "Doe",
                      "company_name": "Google"
                }
                """;

        String responseBody = mockMvc.perform(post("/api/v1/recruiter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recruiter_id").exists())
                .andExpect(jsonPath("$.person_id").exists())
                .andExpect(jsonPath("$.first_name").value("John"))
                .andExpect(jsonPath("$.last_name").value("Doe"))
                .andExpect(jsonPath("$.company_name").value("Google"))
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var recruiterId = JsonPath.read(responseBody, "$.recruiter_id");
        var updatedAt = JsonPath.read(responseBody, "$.updated_at");
        var createdAt = JsonPath.read(responseBody, "$.updated_at");


        // When
        String updateRequest = """
                {
                }
                """;

        // Then
        mockMvc.perform(put("/api/v1/recruiter/{id}", recruiterId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Recruiter updated successfully"));

        mockMvc.perform(get("/api/v1/recruiter/{id}", recruiterId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recruiter_id").value(recruiterId))
                .andExpect(jsonPath("$.person_id").exists())
                .andExpect(jsonPath("$.first_name").value("John"))
                .andExpect(jsonPath("$.last_name").value("Doe"))
                .andExpect(jsonPath("$.company_name").doesNotExist())
                .andExpect(jsonPath("$.created_at").value(createdAt))
                .andExpect(jsonPath("$.updated_at", org.hamcrest.Matchers.not(updatedAt)));
    }

    @Test
    void updateRecruiter_shouldReturnBadRequest_whenPutRequestTryUpdatePerson() throws Exception {
        // Given
        String request = """
                {
                      "first_name": "John",
                      "last_name": "Doe",
                      "company_name": "Google"
                }
                """;

        String responseBody = mockMvc.perform(post("/api/v1/recruiter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recruiter_id").exists())
                .andExpect(jsonPath("$.person_id").exists())
                .andExpect(jsonPath("$.first_name").value("John"))
                .andExpect(jsonPath("$.last_name").value("Doe"))
                .andExpect(jsonPath("$.company_name").value("Google"))
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var recruiterId = JsonPath.read(responseBody, "$.recruiter_id");
        var updatedAt = JsonPath.read(responseBody, "$.updated_at");
        var createdAt = JsonPath.read(responseBody, "$.updated_at");


        // When
        String updateRequest = """
                {
                        "first_name": "Vladyslav",
                        "last_name": "Bondar"
                }
                """;

        // Then
        mockMvc.perform(put("/api/v1/recruiter/{id}", recruiterId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/v1/recruiter/{id}", recruiterId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recruiter_id").value(recruiterId))
                .andExpect(jsonPath("$.person_id").exists())
                .andExpect(jsonPath("$.first_name").value("John"))
                .andExpect(jsonPath("$.last_name").value("Doe"))
                .andExpect(jsonPath("$.company_name").value("Google"))
                .andExpect(jsonPath("$.created_at").value(createdAt))
                .andExpect(jsonPath("$.updated_at").value(updatedAt));

    }

    @Test
    void updateRecruiter_shouldReturnBadRequest_whenPutRequestTryUpdatePersonAndRecruiter() throws Exception {
        // Given
        String request = """
                {
                      "first_name": "John",
                      "last_name": "Doe",
                      "company_name": "Google"
                }
                """;

        String responseBody = mockMvc.perform(post("/api/v1/recruiter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recruiter_id").exists())
                .andExpect(jsonPath("$.person_id").exists())
                .andExpect(jsonPath("$.first_name").value("John"))
                .andExpect(jsonPath("$.last_name").value("Doe"))
                .andExpect(jsonPath("$.company_name").value("Google"))
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var recruiterId = JsonPath.read(responseBody, "$.recruiter_id");
        var updatedAt = JsonPath.read(responseBody, "$.updated_at");
        var createdAt = JsonPath.read(responseBody, "$.updated_at");


        // When
        String updateRequest = """
                {
                        "first_name": "Vladyslav",
                        "last_name": "Bondar",
                        "company_name": "Microsoft"
                }
                """;

        // Then
        mockMvc.perform(put("/api/v1/recruiter/{id}", recruiterId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/v1/recruiter/{id}", recruiterId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recruiter_id").value(recruiterId))
                .andExpect(jsonPath("$.person_id").exists())
                .andExpect(jsonPath("$.first_name").value("John"))
                .andExpect(jsonPath("$.last_name").value("Doe"))
                .andExpect(jsonPath("$.company_name").value("Google"))
                .andExpect(jsonPath("$.created_at").value(createdAt))
                .andExpect(jsonPath("$.updated_at").value(updatedAt));
    }

    @Test
    void updateRecruiter_shouldReturnBadRequest_whenPutRequestWithEmptyData() throws Exception {
        // Given
        String request = """
                {
                      "first_name": "John",
                      "last_name": "Doe",
                      "company_name": "Google"
                }
                """;

        String responseBody = mockMvc.perform(post("/api/v1/recruiter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recruiter_id").exists())
                .andExpect(jsonPath("$.person_id").exists())
                .andExpect(jsonPath("$.first_name").value("John"))
                .andExpect(jsonPath("$.last_name").value("Doe"))
                .andExpect(jsonPath("$.company_name").value("Google"))
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var recruiterId = JsonPath.read(responseBody, "$.recruiter_id");
        var updatedAt = JsonPath.read(responseBody, "$.updated_at");
        var createdAt = JsonPath.read(responseBody, "$.updated_at");

        // When
        String updateRequest = """
                {
                      "company_name": ""
                }
                """;

        // Then
        mockMvc.perform(put("/api/v1/recruiter/{id}", recruiterId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Recruiter updated successfully"));

        mockMvc.perform(get("/api/v1/recruiter/{id}", recruiterId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recruiter_id").value(recruiterId))
                .andExpect(jsonPath("$.person_id").exists())
                .andExpect(jsonPath("$.first_name").value("John"))
                .andExpect(jsonPath("$.last_name").value("Doe"))
                .andExpect(jsonPath("$.company_name").value(""))
                .andExpect(jsonPath("$.created_at").value(createdAt))
                .andExpect(jsonPath("$.updated_at", org.hamcrest.Matchers.not(updatedAt)));
    }

    @Test
    void updateRecruiter_shouldReturnBadRequest_whenPutRequestWithNullData() throws Exception {
        // Given
        String request = """
                {
                      "first_name": "John",
                      "last_name": "Doe",
                      "company_name": "Google"
                }
                """;

        String responseBody = mockMvc.perform(post("/api/v1/recruiter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recruiter_id").exists())
                .andExpect(jsonPath("$.person_id").exists())
                .andExpect(jsonPath("$.first_name").value("John"))
                .andExpect(jsonPath("$.last_name").value("Doe"))
                .andExpect(jsonPath("$.company_name").value("Google"))
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var recruiterId = JsonPath.read(responseBody, "$.recruiter_id");
        var updatedAt = JsonPath.read(responseBody, "$.updated_at");
        var createdAt = JsonPath.read(responseBody, "$.updated_at");

        // When
        String updateRequest = """
                {
                      "company_name": null
                }
                """;

        // Then
        mockMvc.perform(put("/api/v1/recruiter/{id}", recruiterId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Recruiter updated successfully"));

        mockMvc.perform(get("/api/v1/recruiter/{id}", recruiterId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recruiter_id").value(recruiterId))
                .andExpect(jsonPath("$.person_id").exists())
                .andExpect(jsonPath("$.first_name").value("John"))
                .andExpect(jsonPath("$.last_name").value("Doe"))
                .andExpect(jsonPath("$.company_name").doesNotExist())
                .andExpect(jsonPath("$.created_at").value(createdAt))
                .andExpect(jsonPath("$.updated_at", org.hamcrest.Matchers.not(updatedAt)));
    }

    @Test
    void deleteRecruiter_shouldReturnOk_whenRecruiterExists() throws Exception {
        // Given
        String request = """
                {
                      "first_name": "John",
                      "last_name": "Doe",
                      "company_name": "Google"
                }
                """;

        String responseBody = mockMvc.perform(post("/api/v1/recruiter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recruiter_id").exists())
                .andExpect(jsonPath("$.person_id").exists())
                .andExpect(jsonPath("$.first_name").value("John"))
                .andExpect(jsonPath("$.last_name").value("Doe"))
                .andExpect(jsonPath("$.company_name").value("Google"))
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var recruiterId = JsonPath.read(responseBody, "$.recruiter_id");

        // When
        mockMvc.perform(delete("/api/v1/recruiter/{id}", recruiterId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Recruiter deleted successfully"));

        // Then
        mockMvc.perform(get("/api/v1/recruiter/{id}", recruiterId))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteRecruiter_shouldReturnNotFound_whenRecruiterDoesNotExist() throws Exception {
        // Given
        var recruiterId = 2024L;

        // When and Then
        mockMvc.perform(delete("/api/v1/recruiter/{id}", recruiterId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getRecruiterById_shouldReturnOk_whenRecruiterExists() throws Exception {
        // Given
        String request = """
                {
                      "first_name": "John",
                      "last_name": "Doe",
                      "company_name": "Google"
                }
                """;

        String responseBody = mockMvc.perform(post("/api/v1/recruiter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recruiter_id").exists())
                .andExpect(jsonPath("$.person_id").exists())
                .andExpect(jsonPath("$.first_name").value("John"))
                .andExpect(jsonPath("$.last_name").value("Doe"))
                .andExpect(jsonPath("$.company_name").value("Google"))
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var recruiterId = JsonPath.read(responseBody, "$.recruiter_id");

        // When and Then
        mockMvc.perform(get("/api/v1/recruiter/{id}", recruiterId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recruiter_id").value(recruiterId))
                .andExpect(jsonPath("$.person_id").exists())
                .andExpect(jsonPath("$.first_name").value("John"))
                .andExpect(jsonPath("$.last_name").value("Doe"))
                .andExpect(jsonPath("$.company_name").value("Google"))
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());
    }

    @Test
    void getRecruiterById_shouldReturnNotFound_whenRecruiterDoesNotExist() throws Exception {
        // Given
        var recruiterId = 2024L;

        // When and Then
        mockMvc.perform(get("/api/v1/recruiter/{id}", recruiterId))
                .andExpect(status().isNotFound());
    }
}