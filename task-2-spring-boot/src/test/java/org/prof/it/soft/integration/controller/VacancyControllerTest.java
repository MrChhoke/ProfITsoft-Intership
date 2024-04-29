package org.prof.it.soft.integration.controller;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.prof.it.soft.entity.Person;
import org.prof.it.soft.entity.Recruiter;
import org.prof.it.soft.entity.Vacancy;
import org.prof.it.soft.integration.annotation.IT;
import org.prof.it.soft.repo.RecruiterRepository;
import org.prof.it.soft.repo.VacancyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@IT
@AutoConfigureMockMvc
class VacancyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecruiterRepository recruiterRepository;

    @Autowired
    private VacancyRepository vacancyRepository;

    @BeforeEach
    void setUp() {
        vacancyRepository.deleteAll();
        recruiterRepository.deleteAll();
    }

    @Test
    void saveVacancy_shouldReturnOk_whenIdIsCorrect() throws Exception {
        Recruiter savedRecruiter = recruiterRepository.saveAndFlush(
                Recruiter.builder()
                        .companyName("Google")
                        .person(Person.builder()
                                .firstName("Anna")
                                .lastName("Petrov")
                                .build())
                        .build()
        );

        String request = """
                {
                    "position": "Java Developer",
                    "salary": 1000.0,
                    "technology_stack": ["Java", "Spring"],
                    "recruiter_id": %d
                }
                """.formatted(savedRecruiter.getId());

        mockMvc.perform(post("/api/v1/vacancy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.vacancy_id").isNumber())
                .andExpect(jsonPath("$.position").value("Java Developer"))
                .andExpect(jsonPath("$.salary").value(1000.0))
                .andExpect(jsonPath("$.technology_stack").isArray())
                .andExpect(jsonPath("$.technology_stack", hasSize(2)))
                .andExpect(jsonPath("$.technology_stack", hasItem("Java")))
                .andExpect(jsonPath("$.technology_stack", hasItem("Spring")));
    }

    @Test
    void saveVacancy_shouldReturnBadRequest_whenRecruiterIdDoesntExist() throws Exception {
        String request = """
                {
                    "position": "Java Developer",
                    "salary": 1000.0,
                    "technology_stack": ["Java", "Spring"],
                    "recruiter_id": 1
                }
                """;

        mockMvc.perform(post("/api/v1/vacancy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value("Recruiter with id 1 not found"));
    }

    @Test
    void saveVacancy_shouldReturnBadRequest_whenRecruiterIdIsNull() throws Exception {
        String request = """
                {
                    "position": "Java Developer",
                    "salary": 1000.0,
                    "technology_stack": ["Java", "Spring"]
                }
                """;

        mockMvc.perform(post("/api/v1/vacancy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value("Recruiter id is required"));
    }

    @Test
    void saveVacancy_shouldReturnBadRequest_whenPositionIsNull() throws Exception {
        String request = """
                {
                    "salary": 1000.0,
                    "technology_stack": ["Java", "Spring"],
                    "recruiter_id": 1
                }
                """;

        mockMvc.perform(post("/api/v1/vacancy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value("Position is required"));
    }

    @Test
    void saveVacancy_shouldReturnOk_whenSalaryIsNull() throws Exception {
        Recruiter savedRecruiter = recruiterRepository.saveAndFlush(
                Recruiter.builder()
                        .companyName("Google")
                        .person(Person.builder()
                                .firstName("Anna")
                                .lastName("Petrov")
                                .build())
                        .build()
        );

        String request = """
                {
                    "position": "Java Developer",
                    "technology_stack": ["Java", "Spring"],
                    "recruiter_id": %d
                }
                """.formatted(savedRecruiter.getId());

        mockMvc.perform(post("/api/v1/vacancy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.vacancy_id").isNumber())
                .andExpect(jsonPath("$.position").value("Java Developer"))
                .andExpect(jsonPath("$.salary").doesNotExist())
                .andExpect(jsonPath("$.technology_stack").isArray())
                .andExpect(jsonPath("$.technology_stack", hasSize(2)))
                .andExpect(jsonPath("$.technology_stack", hasItem("Java")))
                .andExpect(jsonPath("$.technology_stack", hasItem("Spring")));
    }

    @Test
    void saveVacancy_shouldReturnOk_whenTechnologyStackIsEmpty() throws Exception {
        Recruiter savedRecruiter = recruiterRepository.saveAndFlush(
                Recruiter.builder()
                        .companyName("Google")
                        .person(Person.builder()
                                .firstName("Anna")
                                .lastName("Petrov")
                                .build())
                        .build()
        );

        String request = """
                {
                    "position": "Java Developer",
                    "salary": 1000.0,
                    "technology_stack": [],
                    "recruiter_id": %d
                }
                """.formatted(savedRecruiter.getId());

        mockMvc.perform(post("/api/v1/vacancy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vacancy_id").isNumber())
                .andExpect(jsonPath("$.position").value("Java Developer"))
                .andExpect(jsonPath("$.salary").value(1000.0))
                .andExpect(jsonPath("$.technology_stack").doesNotExist());
    }

    @Test
    void saveVacancy_shouldReturnOk_whenTechnologyStackIsNull() throws Exception {
        Recruiter savedRecruiter = recruiterRepository.saveAndFlush(
                Recruiter.builder()
                        .companyName("Google")
                        .person(Person.builder()
                                .firstName("Anna")
                                .lastName("Petrov")
                                .build())
                        .build()
        );

        String request = """
                {
                    "position": "Java Developer",
                    "salary": 1000.0,
                    "recruiter_id": %d
                }
                """.formatted(savedRecruiter.getId());

        mockMvc.perform(post("/api/v1/vacancy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vacancy_id").isNumber())
                .andExpect(jsonPath("$.position").value("Java Developer"))
                .andExpect(jsonPath("$.salary").value(1000.0))
                .andExpect(jsonPath("$.technology_stack").doesNotExist());
    }

    @Test
    void saveVacancy_shouldReturnBadRequest_whenPositionIsEmpty() throws Exception {
        Recruiter savedRecruiter = recruiterRepository.saveAndFlush(
                Recruiter.builder()
                        .companyName("Google")
                        .person(Person.builder()
                                .firstName("Anna")
                                .lastName("Petrov")
                                .build())
                        .build()
        );

        String request = """
                {
                    "position": "",
                    "salary": 1000.0,
                    "technology_stack": ["Java", "Spring"],
                    "recruiter_id": %d
                }
                """.formatted(savedRecruiter.getId());

        mockMvc.perform(post("/api/v1/vacancy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value("Position is required"));
    }

    @Test
    void saveVacancy_shouldReturnBadRequest_whenSalaryIsNegative() throws Exception {
        Recruiter savedRecruiter = recruiterRepository.saveAndFlush(
                Recruiter.builder()
                        .companyName("Google")
                        .person(Person.builder()
                                .firstName("Anna")
                                .lastName("Petrov")
                                .build())
                        .build()
        );

        String request = """
                {
                    "position": "Java Developer",
                    "salary": -1000.0,
                    "technology_stack": ["Java", "Spring"],
                    "recruiter_id": %d
                }
                """.formatted(savedRecruiter.getId());

        mockMvc.perform(post("/api/v1/vacancy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value("Salary must be greater than 0"));
    }

    @Test
    void saveVacancy_shouldReturnBad_whenSalaryIsZero() throws Exception {
        Recruiter savedRecruiter = recruiterRepository.saveAndFlush(
                Recruiter.builder()
                        .companyName("Google")
                        .person(Person.builder()
                                .firstName("Anna")
                                .lastName("Petrov")
                                .build())
                        .build()
        );

        String request = """
                {
                    "position": "Java Developer",
                    "salary": 0.0,
                    "technology_stack": ["Java", "Spring"],
                    "recruiter_id": %d
                }
                """.formatted(savedRecruiter.getId());

        mockMvc.perform(post("/api/v1/vacancy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value("Salary must be greater than 0"));
    }

    @Test
    void getVacancyById_shouldReturnOk_whenIdIsCorrect() throws Exception {
        // Given
        Recruiter savedRecruiter = recruiterRepository.saveAndFlush(
                Recruiter.builder()
                        .companyName("Google")
                        .person(Person.builder()
                                .firstName("Anna")
                                .lastName("Petrov")
                                .build())
                        .build()
        );

        Vacancy savedVacancy = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Java Developer")
                        .salary(1000.0f)
                        .technologyStack(List.of("Java", "Spring"))
                        .recruiter(savedRecruiter)
                        .build()
        );

        // When and then
        mockMvc.perform(get("/api/v1/vacancy/{id}", savedVacancy.getId()))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.vacancy_id").value(savedVacancy.getId()))
                .andExpect(jsonPath("$.position").value("Java Developer"))
                .andExpect(jsonPath("$.salary").value(1000.0))
                .andExpect(jsonPath("$.technology_stack").isArray())
                .andExpect(jsonPath("$.technology_stack", hasSize(2)))
                .andExpect(jsonPath("$.technology_stack", hasItem("Java")))
                .andExpect(jsonPath("$.technology_stack", hasItem("Spring")))
                .andExpect(jsonPath("$.recruiter.recruiter_id").value(savedRecruiter.getId()))
                .andExpect(jsonPath("$.recruiter.company_name").value("Google"))
                .andExpect(jsonPath("$.recruiter.person_id").value(savedRecruiter.getPerson().getId()))
                .andExpect(jsonPath("$.recruiter.first_name").value("Anna"))
                .andExpect(jsonPath("$.recruiter.last_name").value("Petrov"));
    }

    @Test
    void getVacancyById_shouldReturnNotFound_whenIdIsIncorrect() throws Exception {
        mockMvc.perform(get("/api/v1/vacancy/{id}", 1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value("Vacancy with id 1 not found"));
    }

    @Test
    void updateVacancy_shouldReturnOk_whenIdIsCorrect() throws Exception {
        // Given
        Recruiter savedRecruiter = recruiterRepository.saveAndFlush(
                Recruiter.builder()
                        .companyName("Google")
                        .person(Person.builder()
                                .firstName("Anna")
                                .lastName("Petrov")
                                .build())
                        .build()
        );

        Vacancy savedVacancy = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Java Developer")
                        .salary(1000.0f)
                        .technologyStack(List.of("Java", "Spring"))
                        .recruiter(savedRecruiter)
                        .build()
        );

        String request = """
                {
                    "position": "Python Developer",
                    "salary": 2000.0,
                    "technology_stack": ["Python", "Django"],
                    "recruiter_id": %d
                }
                """.formatted(savedRecruiter.getId());

        // When and then
        mockMvc.perform(put("/api/v1/vacancy/{id}", savedVacancy.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Vacancy updated successfully"));

        Vacancy updatedVacancy = vacancyRepository.findById(savedVacancy.getId()).orElseThrow();

        assertThat(updatedVacancy.getPosition()).isEqualTo("Python Developer");
        assertThat(updatedVacancy.getSalary()).isEqualTo(2000.0f);
        assertThat(updatedVacancy.getTechnologyStack()).containsExactly("Python", "Django");
        assertThat(updatedVacancy.getRecruiter().getId()).isEqualTo(savedRecruiter.getId());
    }

    @Test
    void updateVacancy_shouldReturnNotFound_whenIdIsIncorrect() throws Exception {
        final Long incorrectId = 1L;

        String request = """
                {
                    "position": "Python Developer",
                    "salary": 2000.0,
                    "technology_stack": ["Python", "Django"],
                    "recruiter_id": 1
                }
                """;

        mockMvc.perform(put("/api/v1/vacancy/{id}", incorrectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isNotFound());

        assertThat(vacancyRepository.findById(incorrectId)).isEmpty();
    }

    @Test
    void updateVacancy_shouldReturnBadRequest_whenRecruiterIdDoesntExist() throws Exception {
        // Given
        Recruiter savedRecruiter = recruiterRepository.saveAndFlush(
                Recruiter.builder()
                        .companyName("Google")
                        .person(Person.builder()
                                .firstName("Anna")
                                .lastName("Petrov")
                                .build())
                        .build()
        );

        Vacancy savedVacancy = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Java Developer")
                        .salary(1000.0f)
                        .technologyStack(List.of("Java", "Spring"))
                        .recruiter(savedRecruiter)
                        .build()
        );

        String request = """
                {
                    "position": "Python Developer",
                    "salary": 2000.0,
                    "technology_stack": ["Python", "Django"],
                    "recruiter_id": 2
                }
                """;

        // When and then
        mockMvc.perform(put("/api/v1/vacancy/{id}", savedVacancy.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value("Recruiter with id 2 not found"));
    }

    @Test
    void updateVacancy_shouldReturnBadRequest_whenRecruiterIdIsNull() throws Exception {
        // Given
        Recruiter savedRecruiter = recruiterRepository.saveAndFlush(
                Recruiter.builder()
                        .companyName("Google")
                        .person(Person.builder()
                                .firstName("Anna")
                                .lastName("Petrov")
                                .build())
                        .build()
        );

        Vacancy savedVacancy = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Java Developer")
                        .salary(1000.0f)
                        .technologyStack(List.of("Java", "Spring"))
                        .recruiter(savedRecruiter)
                        .build()
        );

        String request = """
                {
                    "position": "Python Developer",
                    "salary": 2000.0,
                    "technology_stack": ["Python", "Django"]
                }
                """;

        // When and then
        mockMvc.perform(put("/api/v1/vacancy/{id}", savedVacancy.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value("Recruiter id is required"));
    }

    @Test
    void updateVacancy_shouldReturnOk_whenTechnologyStackIsNull() throws Exception {
        // Given
        Recruiter savedRecruiter = recruiterRepository.saveAndFlush(
                Recruiter.builder()
                        .companyName("Google")
                        .person(Person.builder()
                                .firstName("Anna")
                                .lastName("Petrov")
                                .build())
                        .build()
        );

        Vacancy savedVacancy = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Java Developer")
                        .salary(1000.0f)
                        .technologyStack(List.of("Java", "Spring"))
                        .recruiter(savedRecruiter)
                        .build()
        );

        String request = """
                {
                    "position": "Python Developer",
                    "salary": 2000.0,
                    "recruiter_id": %d
                }
                """.formatted(savedRecruiter.getId());

        assertThat(vacancyRepository.findById(savedVacancy.getId()).orElseThrow().getTechnologyStack()).containsExactly("Java", "Spring");

        // When and then
        mockMvc.perform(put("/api/v1/vacancy/{id}", savedVacancy.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Vacancy updated successfully"));

        Vacancy updatedVacancy = vacancyRepository.findById(savedVacancy.getId()).orElseThrow();

        assertThat(updatedVacancy.getPosition()).isEqualTo("Python Developer");
        assertThat(updatedVacancy.getSalary()).isEqualTo(2000.0f);
        assertThat(updatedVacancy.getTechnologyStack()).isEmpty();
        assertThat(updatedVacancy.getRecruiter().getId()).isEqualTo(savedRecruiter.getId());
    }

    @Test
    void updateVacancy_shouldReturnBadRequest_whenSalaryIsNegativeAndPositionIsNullAndRecruiterIsNull() throws Exception {
        // Given
        Recruiter savedRecruiter = recruiterRepository.saveAndFlush(
                Recruiter.builder()
                        .companyName("Google")
                        .person(Person.builder()
                                .firstName("Anna")
                                .lastName("Petrov")
                                .build())
                        .build()
        );

        Vacancy savedVacancy = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Java Developer")
                        .salary(1000.0f)
                        .technologyStack(List.of("Java", "Spring"))
                        .recruiter(savedRecruiter)
                        .build()
        );

        String request = """
                {
                    "salary": -2000.0,
                    "technology_stack": ["Python", "Django"]
                }
                """;

        // When and then
        mockMvc.perform(put("/api/v1/vacancy/{id}", savedVacancy.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors", hasSize(3)))
                .andExpect(jsonPath("$.errors[2]").value("Position is required"))
                .andExpect(jsonPath("$.errors[1]").value("Salary must be greater than 0"))
                .andExpect(jsonPath("$.errors[0]").value("Recruiter id is required"));
    }

    @Test
    void updateVacancy_shouldReturnBadRequest_whenSalaryIsZeroAndPositionIsEmpty() throws Exception {
        // Given
        Recruiter savedRecruiter = recruiterRepository.saveAndFlush(
                Recruiter.builder()
                        .companyName("Google")
                        .person(Person.builder()
                                .firstName("Anna")
                                .lastName("Petrov")
                                .build())
                        .build()
        );

        Vacancy savedVacancy = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Java Developer")
                        .salary(1000.0f)
                        .technologyStack(List.of("Java", "Spring"))
                        .recruiter(savedRecruiter)
                        .build()
        );

        String request = """
                {
                    "position": "",
                    "salary": 0.0,
                    "recruiter_id": %d,
                    "technology_stack": ["Python", "Django"]
                }
                """.formatted(savedRecruiter.getId());

        // When and then
        mockMvc.perform(put("/api/v1/vacancy/{id}", savedVacancy.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors", hasSize(2)))
                .andExpect(jsonPath("$.errors[1]").value("Position is required"))
                .andExpect(jsonPath("$.errors[0]").value("Salary must be greater than 0"));
    }

    @Test
    void deleteVacancy_shouldReturnOk_whenIdIsCorrect() throws Exception {
        // Given
        Recruiter savedRecruiter = recruiterRepository.saveAndFlush(
                Recruiter.builder()
                        .companyName("Google")
                        .person(Person.builder()
                                .firstName("Anna")
                                .lastName("Petrov")
                                .build())
                        .build()
        );

        Vacancy vacancy = Vacancy.builder()
                .position("Java Developer")
                .salary(1000.0f)
                .technologyStack(List.of("Java", "Spring"))
                .recruiter(savedRecruiter)
                .build();

        Vacancy vacancy2 = Vacancy.builder()
                .position("Python Developer")
                .salary(2000.0f)
                .technologyStack(List.of("Python", "Django"))
                .recruiter(savedRecruiter)
                .build();

        Vacancy savedVacancy = vacancyRepository.saveAndFlush(vacancy);
        Vacancy savedVacancy2 = vacancyRepository.saveAndFlush(vacancy2);

        savedRecruiter.addVacancy(vacancy);
        savedRecruiter.addVacancy(vacancy2);

        Long recruiterId = savedVacancy.getRecruiter().getId();

        assertThat(vacancyRepository.findById(savedVacancy.getId())).isPresent();
        assertThat(recruiterRepository.findById(recruiterId).get().getVacancies()).isNotEmpty();

        // When and then
        mockMvc.perform(delete("/api/v1/vacancy/{id}", savedVacancy.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Vacancy deleted successfully"));

        assertThat(vacancyRepository.findById(savedVacancy.getId())).isEmpty();
        assertThat(vacancyRepository.findById(savedVacancy2.getId())).isPresent();

        assertThat(recruiterRepository.findById(recruiterId).get().getVacancies()).hasSize(1);

        mockMvc.perform(delete("/api/v1/vacancy/{id}", savedVacancy2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Vacancy deleted successfully"));

        assertThat(vacancyRepository.findById(savedVacancy2.getId())).isEmpty();
        assertThat(recruiterRepository.findById(recruiterId).get().getVacancies()).hasSize(0);
    }

    @Test
    void deleteVacancy_shouldReturnNotFound_whenIdIsIncorrect() throws Exception {
        mockMvc.perform(delete("/api/v1/vacancy/{id}", 1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value("Vacancy with id 1 not found"));
    }

    @Test
    void deleteVacancy_shouldReturnNotFound_whenIdIsIncorrect_2() throws Exception {
        // Given
        Recruiter savedRecruiter = recruiterRepository.saveAndFlush(
                Recruiter.builder()
                        .companyName("Google")
                        .person(Person.builder()
                                .firstName("Anna")
                                .lastName("Petrov")
                                .build())
                        .build()
        );

        Vacancy vacancy = Vacancy.builder()
                .position("Java Developer")
                .salary(1000.0f)
                .technologyStack(List.of("Java", "Spring"))
                .recruiter(savedRecruiter)
                .build();

        Vacancy vacancy2 = Vacancy.builder()
                .position("Python Developer")
                .salary(2000.0f)
                .technologyStack(List.of("Python", "Django"))
                .recruiter(savedRecruiter)
                .build();

        Vacancy savedVacancy = vacancyRepository.saveAndFlush(vacancy);
        Vacancy savedVacancy2 = vacancyRepository.saveAndFlush(vacancy2);

        savedRecruiter.addVacancy(savedVacancy);
        savedRecruiter.addVacancy(savedVacancy2);

        Long recruiterId = savedVacancy.getRecruiter().getId();

        assertThat(vacancyRepository.findById(savedVacancy.getId())).isPresent();
        assertThat(recruiterRepository.findById(recruiterId).get().getVacancies()).isNotEmpty();
        assertThat(recruiterRepository.findById(recruiterId).get().getVacancies()).hasSize(2);

        // When and then
        mockMvc.perform(delete("/api/v1/vacancy/{id}", 2))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value("Vacancy with id 2 not found"));

        assertThat(vacancyRepository.findById(savedVacancy.getId())).isPresent();
        assertThat(recruiterRepository.findById(recruiterId).get().getVacancies()).isNotEmpty();
        assertThat(recruiterRepository.findById(recruiterId).get().getVacancies()).hasSize(2);
    }

    @Test
    void getFilteredVacancies_shouldReturnOk_whenFilterContainsOnlyPageNumberAndPageSize() throws Exception {
        // Given
        Recruiter savedRecruiter = recruiterRepository.saveAndFlush(
                Recruiter.builder()
                        .companyName("Google")
                        .person(Person.builder()
                                .firstName("Anna")
                                .lastName("Petrov")
                                .build())
                        .build()
        );

        Vacancy savedVacancy1 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Java Developer")
                        .salary(1000.0f)
                        .technologyStack(List.of("Java", "Spring"))
                        .recruiter(savedRecruiter)
                        .build()
        );

        Vacancy savedVacancy2 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Python Developer")
                        .salary(2000.0f)
                        .technologyStack(List.of("Python", "Django"))
                        .recruiter(savedRecruiter)
                        .build()
        );

        String request = """
                {
                    "page": 0,
                    "size": 10
                }
                """;

        // When and then
        mockMvc.perform(post("/api/v1/vacancy/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.pageable.pageSize").value(10))
                .andExpect(jsonPath("$.numberOfElements").value(2))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].vacancy_id").value(savedVacancy1.getId()))
                .andExpect(jsonPath("$.content[0].position").value("Java Developer"))
                .andExpect(jsonPath("$.content[0].salary").value(1000.0))
                .andExpect(jsonPath("$.content[0].technology_stack").isArray())
                .andExpect(jsonPath("$.content[0].technology_stack", hasSize(2)))
                .andExpect(jsonPath("$.content[0].technology_stack", hasItem("Java")))
                .andExpect(jsonPath("$.content[0].technology_stack", hasItem("Spring")))
                .andExpect(jsonPath("$.content[0].recruiter.recruiter_id").value(savedRecruiter.getId()))
                .andExpect(jsonPath("$.content[0].recruiter.company_name").value("Google"))
                .andExpect(jsonPath("$.content[0].recruiter.person_id").value(savedRecruiter.getPerson().getId()))
                .andExpect(jsonPath("$.content[0].recruiter.first_name").value("Anna"))
                .andExpect(jsonPath("$.content[0].recruiter.last_name").value("Petrov"))
                .andExpect(jsonPath("$.content[1].vacancy_id").value(savedVacancy2.getId()))
                .andExpect(jsonPath("$.content[1].position").value("Python Developer"))
                .andExpect(jsonPath("$.content[1].salary").value(2000.0))
                .andExpect(jsonPath("$.content[1].technology_stack").isArray())
                .andExpect(jsonPath("$.content[1].technology_stack", hasSize(2)))
                .andExpect(jsonPath("$.content[1].technology_stack", hasItem("Python")))
                .andExpect(jsonPath("$.content[1].technology_stack", hasItem("Django")))
                .andExpect(jsonPath("$.content[1].recruiter.recruiter_id").value(savedRecruiter.getId()))
                .andExpect(jsonPath("$.content[1].recruiter.company_name").value("Google"))
                .andExpect(jsonPath("$.content[1].recruiter.person_id").value(savedRecruiter.getPerson().getId()))
                .andExpect(jsonPath("$.content[1].recruiter.first_name").value("Anna"))
                .andExpect(jsonPath("$.content[1].recruiter.last_name").value("Petrov"));
    }

    @Test
    void getFilteredVacancies_shouldReturnOk_whenFilterContainsOnlyPosition() throws Exception {
        // Given
        Recruiter savedRecruiter = recruiterRepository.saveAndFlush(
                Recruiter.builder()
                        .companyName("Google")
                        .person(Person.builder()
                                .firstName("Anna")
                                .lastName("Petrov")
                                .build())
                        .build()
        );

        Vacancy savedVacancy1 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Java Developer")
                        .salary(1000.0f)
                        .technologyStack(List.of("Java", "Spring"))
                        .recruiter(savedRecruiter)
                        .build()
        );

        Vacancy savedVacancy2 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Python Developer")
                        .salary(2000.0f)
                        .technologyStack(List.of("Python", "Django"))
                        .recruiter(savedRecruiter)
                        .build()
        );

        Vacancy savedVacancy3 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Java Developer")
                        .salary(3000.0f)
                        .technologyStack(List.of("Java", "Spring", "Hibernate"))
                        .recruiter(savedRecruiter)
                        .build()
        );

        Vacancy savedVacancy4 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Sql Developer")
                        .salary(4000.0f)
                        .technologyStack(List.of("SQL", "PostgreSQL"))
                        .recruiter(savedRecruiter)
                        .build()
        );

        String request = """
                {
                    "position": "Java Developer",
                    "page": 0,
                    "size": 10
                }
                """;

        // When and then
        mockMvc.perform(post("/api/v1/vacancy/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.pageable.pageSize").value(10))
                .andExpect(jsonPath("$.numberOfElements").value(2))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].vacancy_id").value(savedVacancy1.getId()))
                .andExpect(jsonPath("$.content[0].position").value("Java Developer"))
                .andExpect(jsonPath("$.content[0].salary").value(1000.0))
                .andExpect(jsonPath("$.content[0].technology_stack").isArray())
                .andExpect(jsonPath("$.content[0].technology_stack", hasSize(2)))
                .andExpect(jsonPath("$.content[0].technology_stack", hasItem("Java")))
                .andExpect(jsonPath("$.content[0].technology_stack", hasItem("Spring")))
                .andExpect(jsonPath("$.content[0].recruiter.recruiter_id").value(savedRecruiter.getId()))
                .andExpect(jsonPath("$.content[0].recruiter.company_name").value("Google"))
                .andExpect(jsonPath("$.content[0].recruiter.person_id").value(savedRecruiter.getPerson().getId()))
                .andExpect(jsonPath("$.content[0].recruiter.first_name").value("Anna"))
                .andExpect(jsonPath("$.content[0].recruiter.last_name").value("Petrov"))
                .andExpect(jsonPath("$.content[1].vacancy_id").value(savedVacancy3.getId()))
                .andExpect(jsonPath("$.content[1].position").value("Java Developer"))
                .andExpect(jsonPath("$.content[1].salary").value(3000.0))
                .andExpect(jsonPath("$.content[1].technology_stack").isArray())
                .andExpect(jsonPath("$.content[1].technology_stack", hasSize(3)))
                .andExpect(jsonPath("$.content[1].technology_stack", hasItem("Java")))
                .andExpect(jsonPath("$.content[1].technology_stack", hasItem("Spring")))
                .andExpect(jsonPath("$.content[1].technology_stack", hasItem("Hibernate")))
                .andExpect(jsonPath("$.content[1].recruiter.recruiter_id").value(savedRecruiter.getId()))
                .andExpect(jsonPath("$.content[1].recruiter.company_name").value("Google"))
                .andExpect(jsonPath("$.content[1].recruiter.person_id").value(savedRecruiter.getPerson().getId()))
                .andExpect(jsonPath("$.content[1].recruiter.first_name").value("Anna"))
                .andExpect(jsonPath("$.content[1].recruiter.last_name").value("Petrov"));
    }

    @Test
    void getFilteredVacancies_shouldReturnOk_whenFilterContainsPositionAndSalary() throws Exception {
        // Given
        Recruiter savedRecruiter = recruiterRepository.saveAndFlush(
                Recruiter.builder()
                        .companyName("Google")
                        .person(Person.builder()
                                .firstName("Anna")
                                .lastName("Petrov")
                                .build())
                        .build()
        );

        Vacancy savedVacancy1 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Java Developer")
                        .salary(1000.0f)
                        .technologyStack(List.of("Java", "Spring"))
                        .recruiter(savedRecruiter)
                        .build()
        );

        Vacancy savedVacancy2 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Python Developer")
                        .salary(2000.0f)
                        .technologyStack(List.of("Python", "Django"))
                        .recruiter(savedRecruiter)
                        .build()
        );

        Vacancy savedVacancy3 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Java Developer")
                        .salary(3000.0f)
                        .technologyStack(List.of("Java", "Spring", "Hibernate"))
                        .recruiter(savedRecruiter)
                        .build()
        );

        Vacancy savedVacancy4 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Sql Developer")
                        .salary(4000.0f)
                        .technologyStack(List.of("SQL", "PostgreSQL"))
                        .recruiter(savedRecruiter)
                        .build()
        );

        String request = """
                {
                    "position": "Java Developer",
                    "min_salary": 500.0,
                    "max_salary": 2500.0,
                    "page": 0,
                    "size": 10
                }
                """;

        assertThat(vacancyRepository.findAll()).hasSize(4);

        // When and then
        mockMvc.perform(post("/api/v1/vacancy/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.pageable.pageSize").value(10))
                .andExpect(jsonPath("$.numberOfElements").value(1))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].vacancy_id").value(savedVacancy1.getId()))
                .andExpect(jsonPath("$.content[0].position").value("Java Developer"))
                .andExpect(jsonPath("$.content[0].salary").value(1000.0))
                .andExpect(jsonPath("$.content[0].technology_stack").isArray())
                .andExpect(jsonPath("$.content[0].technology_stack", hasSize(2)))
                .andExpect(jsonPath("$.content[0].technology_stack", hasItem("Java")))
                .andExpect(jsonPath("$.content[0].technology_stack", hasItem("Spring")))
                .andExpect(jsonPath("$.content[0].recruiter.recruiter_id").value(savedRecruiter.getId()))
                .andExpect(jsonPath("$.content[0].recruiter.company_name").value("Google"))
                .andExpect(jsonPath("$.content[0].recruiter.person_id").value(savedRecruiter.getPerson().getId()))
                .andExpect(jsonPath("$.content[0].recruiter.first_name").value("Anna"))
                .andExpect(jsonPath("$.content[0].recruiter.last_name").value("Petrov"));
    }

    @Test
    void getFilteredVacancies_shouldReturnOk_whenFilterContainsSalaryAndTechnologyStack() throws Exception {
        // Given
        Recruiter savedRecruiter = recruiterRepository.saveAndFlush(
                Recruiter.builder()
                        .companyName("Google")
                        .person(Person.builder()
                                .firstName("Anna")
                                .lastName("Petrov")
                                .build())
                        .build()
        );

        Vacancy savedVacancy1 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Java Developer")
                        .salary(1000.0f)
                        .technologyStack(List.of("Java", "Spring"))
                        .recruiter(savedRecruiter)
                        .build()
        );

        Vacancy savedVacancy2 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Python Developer")
                        .salary(2000.0f)
                        .technologyStack(List.of("Python", "Django"))
                        .recruiter(savedRecruiter)
                        .build()
        );

        Vacancy savedVacancy3 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Java Developer")
                        .salary(3000.0f)
                        .technologyStack(List.of("Java", "Spring", "Hibernate", "PostgreSQL"))
                        .recruiter(savedRecruiter)
                        .build()
        );

        Vacancy savedVacancy4 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Sql Developer")
                        .salary(4000.0f)
                        .technologyStack(List.of("SQL", "PostgreSQL"))
                        .recruiter(savedRecruiter)
                        .build()
        );

        String request = """
                {
                    "technology_stack": ["PostgreSQL"],
                    "min_salary": 3500.0,
                    "page": 0,
                    "size": 10
                }
                """;

        assertThat(vacancyRepository.findAll()).hasSize(4);

        // When and then
        mockMvc.perform(post("/api/v1/vacancy/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.pageable.pageSize").value(10))
                .andExpect(jsonPath("$.numberOfElements").value(1))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].vacancy_id").value(savedVacancy4.getId()))
                .andExpect(jsonPath("$.content[0].position").value("Sql Developer"))
                .andExpect(jsonPath("$.content[0].salary").value(4000.0))
                .andExpect(jsonPath("$.content[0].technology_stack").isArray())
                .andExpect(jsonPath("$.content[0].technology_stack", hasSize(2)))
                .andExpect(jsonPath("$.content[0].technology_stack", hasItem("SQL")))
                .andExpect(jsonPath("$.content[0].technology_stack", hasItem("PostgreSQL")))
                .andExpect(jsonPath("$.content[0].recruiter.recruiter_id").value(savedRecruiter.getId()))
                .andExpect(jsonPath("$.content[0].recruiter.company_name").value("Google"))
                .andExpect(jsonPath("$.content[0].recruiter.person_id").value(savedRecruiter.getPerson().getId()))
                .andExpect(jsonPath("$.content[0].recruiter.first_name").value("Anna"))
                .andExpect(jsonPath("$.content[0].recruiter.last_name").value("Petrov"));
    }

    @Test
    void getFilteredVacancies_shouldReturnOk_whenFilterContainsTechnologyStackAndCompanyName() throws Exception {
        // Given
        Recruiter savedRecruiter1 = recruiterRepository.saveAndFlush(
                Recruiter.builder()
                        .companyName("Google")
                        .person(Person.builder()
                                .firstName("Anna")
                                .lastName("Petrov")
                                .build())
                        .build()
        );

        Recruiter savedRecruiter2 = recruiterRepository.saveAndFlush(
                Recruiter.builder()
                        .companyName("Microsoft")
                        .person(Person.builder()
                                .firstName("John")
                                .lastName("Smith")
                                .build())
                        .build()
        );

        Vacancy savedVacancy1 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Java Developer")
                        .salary(1000.0f)
                        .technologyStack(List.of("Java", "Spring", "Hibernate", "PostgreSQL"))
                        .recruiter(savedRecruiter1)
                        .build()
        );

        Vacancy savedVacancy2 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Python Developer")
                        .salary(2000.0f)
                        .technologyStack(List.of("Python", "Django"))
                        .recruiter(savedRecruiter1)
                        .build()
        );

        Vacancy savedVacancy3 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Java Developer")
                        .salary(3000.0f)
                        .technologyStack(List.of("Java", "Spring", "Hibernate", "PostgreSQL"))
                        .recruiter(savedRecruiter2)
                        .build()
        );

        Vacancy savedVacancy4 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Sql Developer")
                        .salary(4000.0f)
                        .technologyStack(List.of("SQL", "PostgreSQL"))
                        .recruiter(savedRecruiter2)
                        .build()
        );

        String request = """
                {
                    "technology_stack": ["PostgreSQL", "Hibernate"],
                    "company_name": "Microsoft",
                    "page": 0,
                    "size": 10
                }
                """;

        assertThat(vacancyRepository.findAll()).hasSize(4);

        // When and then
        mockMvc.perform(post("/api/v1/vacancy/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.pageable.pageSize").value(10))
                .andExpect(jsonPath("$.numberOfElements").value(1))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].vacancy_id").value(savedVacancy3.getId()))
                .andExpect(jsonPath("$.content[0].position").value("Java Developer"))
                .andExpect(jsonPath("$.content[0].salary").value(3000.0))
                .andExpect(jsonPath("$.content[0].technology_stack").isArray())
                .andExpect(jsonPath("$.content[0].technology_stack", hasSize(4)))
                .andExpect(jsonPath("$.content[0].technology_stack", hasItem("Java")))
                .andExpect(jsonPath("$.content[0].technology_stack", hasItem("Spring")))
                .andExpect(jsonPath("$.content[0].technology_stack", hasItem("Hibernate")))
                .andExpect(jsonPath("$.content[0].technology_stack", hasItem("PostgreSQL")))
                .andExpect(jsonPath("$.content[0].recruiter.company_name").value("Microsoft"))
                .andExpect(jsonPath("$.content[0].recruiter.recruiter_id").value(savedRecruiter2.getId()))
                .andExpect(jsonPath("$.content[0].recruiter.person_id").value(savedRecruiter2.getPerson().getId()))
                .andExpect(jsonPath("$.content[0].recruiter.first_name").value("John"))
                .andExpect(jsonPath("$.content[0].recruiter.last_name").value("Smith"));
    }

    @Test
    void getFilteredVacancies_shouldReturnOk_whenFilterContainsRecruiterIdAndTechnologyStack() throws Exception {
        // Given
        Recruiter savedRecruiter1 = recruiterRepository.saveAndFlush(
                Recruiter.builder()
                        .companyName("Google")
                        .person(Person.builder()
                                .firstName("Anna")
                                .lastName("Petrov")
                                .build())
                        .build()
        );

        Recruiter savedRecruiter2 = recruiterRepository.saveAndFlush(
                Recruiter.builder()
                        .companyName("Microsoft")
                        .person(Person.builder()
                                .firstName("John")
                                .lastName("Smith")
                                .build())
                        .build()
        );

        Vacancy savedVacancy1 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Java Developer")
                        .salary(1000.0f)
                        .technologyStack(List.of("Java", "Spring", "Hibernate", "PostgreSQL"))
                        .recruiter(savedRecruiter1)
                        .build()
        );

        Vacancy savedVacancy2 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Sql Developer")
                        .salary(4000.0f)
                        .technologyStack(List.of("SQL", "PostgreSQL", "MySQL"))
                        .recruiter(savedRecruiter1)
                        .build()
        );

        Vacancy savedVacancy3 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Java Developer")
                        .salary(3000.0f)
                        .technologyStack(List.of("Java", "Spring", "Hibernate", "PostgreSQL"))
                        .recruiter(savedRecruiter2)
                        .build()
        );

        Vacancy savedVacancy4 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Sql Developer")
                        .salary(4000.0f)
                        .technologyStack(List.of("SQL", "PostgreSQL", "MySQL"))
                        .recruiter(savedRecruiter2)
                        .build()
        );

        String request = """
                {
                    "technology_stack": ["PostgreSQL", "SQL"],
                    "recruiter_id": %d,
                    "page": 0,
                    "size": 10
                }
                """.formatted(savedRecruiter2.getId());

        assertThat(vacancyRepository.findAll()).hasSize(4);

        // When and then
        mockMvc.perform(post("/api/v1/vacancy/_list")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.pageable.pageSize").value(10))
                .andExpect(jsonPath("$.numberOfElements").value(1))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].vacancy_id").value(savedVacancy4.getId()))
                .andExpect(jsonPath("$.content[0].position").value("Sql Developer"))
                .andExpect(jsonPath("$.content[0].salary").value(4000.0))
                .andExpect(jsonPath("$.content[0].technology_stack").isArray())
                .andExpect(jsonPath("$.content[0].technology_stack", hasSize(3)))
                .andExpect(jsonPath("$.content[0].technology_stack", hasItem("SQL")))
                .andExpect(jsonPath("$.content[0].technology_stack", hasItem("PostgreSQL")))
                .andExpect(jsonPath("$.content[0].technology_stack", hasItem("MySQL")))
                .andExpect(jsonPath("$.content[0].recruiter.company_name").value("Microsoft"))
                .andExpect(jsonPath("$.content[0].recruiter.recruiter_id").value(savedRecruiter2.getId()))
                .andExpect(jsonPath("$.content[0].recruiter.person_id").value(savedRecruiter2.getPerson().getId()))
                .andExpect(jsonPath("$.content[0].recruiter.first_name").value("John"))
                .andExpect(jsonPath("$.content[0].recruiter.last_name").value("Smith"));
    }

    @Test
    void generateReportExcel_shouldReturnOk_whenFilterContainsRecruiterIdAndTechnologyStack() throws Exception {
        // Given
        Recruiter savedRecruiter1 = recruiterRepository.saveAndFlush(
                Recruiter.builder()
                        .companyName("Google")
                        .person(Person.builder()
                                .firstName("Anna")
                                .lastName("Petrov")
                                .build())
                        .build()
        );

        Recruiter savedRecruiter2 = recruiterRepository.saveAndFlush(
                Recruiter.builder()
                        .companyName("Microsoft")
                        .person(Person.builder()
                                .firstName("John")
                                .lastName("Smith")
                                .build())
                        .build()
        );

        Vacancy savedVacancy1 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Java Developer")
                        .salary(1000.0f)
                        .technologyStack(List.of("Java", "Spring", "Hibernate", "PostgreSQL"))
                        .recruiter(savedRecruiter1)
                        .build()
        );

        Vacancy savedVacancy2 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Sql Developer")
                        .salary(4000.0f)
                        .technologyStack(List.of("SQL", "PostgreSQL", "MySQL"))
                        .recruiter(savedRecruiter1)
                        .build()
        );

        Vacancy savedVacancy3 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Java Developer")
                        .salary(3000.0f)
                        .technologyStack(List.of("Java", "Spring", "Hibernate", "PostgreSQL"))
                        .recruiter(savedRecruiter2)
                        .build()
        );

        Vacancy savedVacancy4 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Sql Developer")
                        .salary(4000.0f)
                        .technologyStack(List.of("SQL", "PostgreSQL", "MySQL"))
                        .recruiter(savedRecruiter2)
                        .build()
        );

        String request = """
                {
                    "technology_stack": ["PostgreSQL", "SQL"],
                    "recruiter_id": %d
                }
                """.formatted(savedRecruiter2.getId());

        assertThat(vacancyRepository.findAll()).hasSize(4);

        // When and then
        MockHttpServletResponse response = mockMvc.perform(post("/api/v1/vacancy/_report")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, startsWith("attachment; filename=vacancies_")))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, endsWith(".xlsx")))
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsByteArray()).isNotEmpty();

        byte[] contentAsByteArray = response.getContentAsByteArray();
        XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(contentAsByteArray));

        assertThat(workbook.getNumberOfSheets()).isEqualTo(1);
        XSSFSheet vacancySheet = workbook.getSheetAt(0);
        assertThat(vacancySheet.getSheetName()).isEqualTo("Vacancies");
        assertThat(vacancySheet.getPhysicalNumberOfRows()).isEqualTo(2);

        assertThat(vacancySheet.getRow(0).getCell(0).getStringCellValue()).isEqualTo("Vacancy ID");
        assertThat(vacancySheet.getRow(0).getCell(1).getStringCellValue()).isEqualTo("Position");
        assertThat(vacancySheet.getRow(0).getCell(2).getStringCellValue()).isEqualTo("Salary");
        assertThat(vacancySheet.getRow(0).getCell(3).getStringCellValue()).isEqualTo("Technology Stack");
        assertThat(vacancySheet.getRow(0).getCell(4).getStringCellValue()).isEqualTo("Company Name");
        assertThat(vacancySheet.getRow(0).getCell(5).getStringCellValue()).isEqualTo("Created At");
        assertThat(vacancySheet.getRow(0).getCell(6).getStringCellValue()).isEqualTo("Recruiter_id");
        assertThat(vacancySheet.getRow(0).getCell(7).getStringCellValue()).isEqualTo("Recruiter First Name");
        assertThat(vacancySheet.getRow(0).getCell(8).getStringCellValue()).isEqualTo("Recruiter Last Name");

        assertThat(vacancySheet.getRow(1).getCell(0).getNumericCellValue()).isEqualTo(savedVacancy4.getId().doubleValue());
        assertThat(vacancySheet.getRow(1).getCell(1).getStringCellValue()).isEqualTo(savedVacancy4.getPosition());
        assertThat(vacancySheet.getRow(1).getCell(2).getNumericCellValue()).isEqualTo(savedVacancy4.getSalary().doubleValue());
        assertThat(vacancySheet.getRow(1).getCell(3).getStringCellValue()).isEqualTo("SQL, PostgreSQL, MySQL");
        assertThat(vacancySheet.getRow(1).getCell(4).getStringCellValue()).isEqualTo(savedRecruiter2.getCompanyName());
        assertThat(vacancySheet.getRow(1).getCell(5).getStringCellValue()).isEqualTo(savedVacancy4.getCreatedAt().toString());
        assertThat(vacancySheet.getRow(1).getCell(6).getNumericCellValue()).isEqualTo(savedRecruiter2.getId().doubleValue());
        assertThat(vacancySheet.getRow(1).getCell(7).getStringCellValue()).isEqualTo(savedRecruiter2.getPerson().getFirstName());
        assertThat(vacancySheet.getRow(1).getCell(8).getStringCellValue()).isEqualTo(savedRecruiter2.getPerson().getLastName());
    }

    @Test
    void generateReportExcel_shouldReturnOk_whenFilterContainsTechnologyStackAndCompanyName() throws Exception {
        // Given
        Recruiter savedRecruiter1 = recruiterRepository.saveAndFlush(
                Recruiter.builder()
                        .companyName("Google")
                        .person(Person.builder()
                                .firstName("Anna")
                                .lastName("Petrov")
                                .build())
                        .build()
        );

        Recruiter savedRecruiter2 = recruiterRepository.saveAndFlush(
                Recruiter.builder()
                        .companyName("Microsoft")
                        .person(Person.builder()
                                .firstName("John")
                                .lastName("Smith")
                                .build())
                        .build()
        );

        Vacancy savedVacancy1 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Java Developer")
                        .salary(1000.0f)
                        .technologyStack(List.of("Java", "Spring", "Hibernate", "PostgreSQL", "SQL"))
                        .recruiter(savedRecruiter1)
                        .build()
        );

        Vacancy savedVacancy2 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Sql Developer")
                        .salary(4000.0f)
                        .technologyStack(List.of("SQL", "PostgreSQL", "MySQL"))
                        .recruiter(savedRecruiter1)
                        .build()
        );

        Vacancy savedVacancy3 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Java Developer")
                        .salary(3000.0f)
                        .technologyStack(List.of("Java", "Spring", "Hibernate", "PostgreSQL", "SQL"))
                        .recruiter(savedRecruiter2)
                        .build()
        );

        Vacancy savedVacancy4 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Sql Developer")
                        .salary(4000.0f)
                        .technologyStack(List.of("SQL", "PostgreSQL", "MySQL"))
                        .recruiter(savedRecruiter2)
                        .build()
        );

        String request = """
                {
                    "technology_stack": ["PostgreSQL", "SQL"],
                    "company_name": "Microsoft"
                }
                """;

        assertThat(vacancyRepository.findAll()).hasSize(4);

        // When and then
        MockHttpServletResponse response = mockMvc.perform(post("/api/v1/vacancy/_report")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, startsWith("attachment; filename=vacancies_")))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, endsWith(".xlsx")))
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsByteArray()).isNotEmpty();

        byte[] contentAsByteArray = response.getContentAsByteArray();

        XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(contentAsByteArray));

        XSSFSheet vacancySheet = workbook.getSheetAt(0);
        assertThat(vacancySheet.getSheetName()).isEqualTo("Vacancies");
        assertThat(vacancySheet.getPhysicalNumberOfRows()).isEqualTo(3);

        assertThat(vacancySheet.getRow(0).getCell(0).getStringCellValue()).isEqualTo("Vacancy ID");
        assertThat(vacancySheet.getRow(0).getCell(1).getStringCellValue()).isEqualTo("Position");
        assertThat(vacancySheet.getRow(0).getCell(2).getStringCellValue()).isEqualTo("Salary");
        assertThat(vacancySheet.getRow(0).getCell(3).getStringCellValue()).isEqualTo("Technology Stack");
        assertThat(vacancySheet.getRow(0).getCell(4).getStringCellValue()).isEqualTo("Company Name");
        assertThat(vacancySheet.getRow(0).getCell(5).getStringCellValue()).isEqualTo("Created At");
        assertThat(vacancySheet.getRow(0).getCell(6).getStringCellValue()).isEqualTo("Recruiter_id");
        assertThat(vacancySheet.getRow(0).getCell(7).getStringCellValue()).isEqualTo("Recruiter First Name");
        assertThat(vacancySheet.getRow(0).getCell(8).getStringCellValue()).isEqualTo("Recruiter Last Name");


        assertThat(vacancySheet.getRow(1).getCell(0).getNumericCellValue()).isEqualTo(savedVacancy3.getId().doubleValue());
        assertThat(vacancySheet.getRow(1).getCell(1).getStringCellValue()).isEqualTo(savedVacancy3.getPosition());
        assertThat(vacancySheet.getRow(1).getCell(2).getNumericCellValue()).isEqualTo(savedVacancy3.getSalary().doubleValue());
        assertThat(vacancySheet.getRow(1).getCell(3).getStringCellValue()).isEqualTo("Java, Spring, Hibernate, PostgreSQL, SQL");
        assertThat(vacancySheet.getRow(1).getCell(4).getStringCellValue()).isEqualTo(savedRecruiter2.getCompanyName());
        assertThat(vacancySheet.getRow(1).getCell(5).getStringCellValue()).isEqualTo(savedVacancy3.getCreatedAt().toString());
        assertThat(vacancySheet.getRow(1).getCell(6).getNumericCellValue()).isEqualTo(savedRecruiter2.getId().doubleValue());
        assertThat(vacancySheet.getRow(1).getCell(7).getStringCellValue()).isEqualTo(savedRecruiter2.getPerson().getFirstName());
        assertThat(vacancySheet.getRow(1).getCell(8).getStringCellValue()).isEqualTo(savedRecruiter2.getPerson().getLastName());

        assertThat(vacancySheet.getRow(2).getCell(0).getNumericCellValue()).isEqualTo(savedVacancy4.getId().doubleValue());
        assertThat(vacancySheet.getRow(2).getCell(1).getStringCellValue()).isEqualTo(savedVacancy4.getPosition());
        assertThat(vacancySheet.getRow(2).getCell(2).getNumericCellValue()).isEqualTo(savedVacancy4.getSalary().doubleValue());
        assertThat(vacancySheet.getRow(2).getCell(3).getStringCellValue()).isEqualTo("SQL, PostgreSQL, MySQL");
        assertThat(vacancySheet.getRow(2).getCell(4).getStringCellValue()).isEqualTo(savedRecruiter2.getCompanyName());
        assertThat(vacancySheet.getRow(2).getCell(5).getStringCellValue()).isEqualTo(savedVacancy4.getCreatedAt().toString());
        assertThat(vacancySheet.getRow(2).getCell(6).getNumericCellValue()).isEqualTo(savedRecruiter2.getId().doubleValue());
        assertThat(vacancySheet.getRow(2).getCell(7).getStringCellValue()).isEqualTo(savedRecruiter2.getPerson().getFirstName());
        assertThat(vacancySheet.getRow(2).getCell(8).getStringCellValue()).isEqualTo(savedRecruiter2.getPerson().getLastName());
    }

    @Test
    void generateReportExcel_shouldReturnOk_whenVacanciesListIsEmpty() throws Exception {
        // Given
        Recruiter savedRecruiter = recruiterRepository.saveAndFlush(
                Recruiter.builder()
                        .companyName("Google")
                        .person(Person.builder()
                                .firstName("Anna")
                                .lastName("Petrov")
                                .build())
                        .build()
        );

        Vacancy savedVacancy1 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Java Developer")
                        .salary(1000.0f)
                        .technologyStack(List.of("Java", "Spring"))
                        .recruiter(savedRecruiter)
                        .build()
        );

        Vacancy savedVacancy2 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Python Developer")
                        .salary(2000.0f)
                        .technologyStack(List.of("Python", "Django"))
                        .recruiter(savedRecruiter)
                        .build()
        );

        Vacancy savedVacancy3 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Java Developer")
                        .salary(3000.0f)
                        .technologyStack(List.of("Java", "Spring", "Hibernate"))
                        .recruiter(savedRecruiter)
                        .build()
        );


        String request = """
                {
                    "position": "PhP Developer"
                }
                """;

        assertThat(vacancyRepository.findAll()).hasSize(3);

        // When and then
        MockHttpServletResponse response = mockMvc.perform(post("/api/v1/vacancy/_report")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, startsWith("attachment; filename=vacancies_")))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, endsWith(".xlsx")))
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsByteArray()).isNotEmpty();

        byte[] contentAsByteArray = response.getContentAsByteArray();

        XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(contentAsByteArray));

        XSSFSheet vacancySheet = workbook.getSheetAt(0);
        assertThat(vacancySheet.getSheetName()).isEqualTo("Vacancies");
        assertThat(vacancySheet.getPhysicalNumberOfRows()).isEqualTo(1); // Only header

        assertThat(vacancySheet.getRow(0).getCell(0).getStringCellValue()).isEqualTo("Vacancy ID");
        assertThat(vacancySheet.getRow(0).getCell(1).getStringCellValue()).isEqualTo("Position");
        assertThat(vacancySheet.getRow(0).getCell(2).getStringCellValue()).isEqualTo("Salary");
        assertThat(vacancySheet.getRow(0).getCell(3).getStringCellValue()).isEqualTo("Technology Stack");
        assertThat(vacancySheet.getRow(0).getCell(4).getStringCellValue()).isEqualTo("Company Name");
        assertThat(vacancySheet.getRow(0).getCell(5).getStringCellValue()).isEqualTo("Created At");
        assertThat(vacancySheet.getRow(0).getCell(6).getStringCellValue()).isEqualTo("Recruiter_id");
        assertThat(vacancySheet.getRow(0).getCell(7).getStringCellValue()).isEqualTo("Recruiter First Name");
        assertThat(vacancySheet.getRow(0).getCell(8).getStringCellValue()).isEqualTo("Recruiter Last Name");
    }

    @Test
    void generateReportExcel_shouldReturnOk_whenFilterContainsPositionAndMinSalaryAndRecruiterIdAndTechnologyStack() throws Exception {
        // Given
        Recruiter savedRecruiter1 = recruiterRepository.saveAndFlush(
                Recruiter.builder()
                        .companyName("Google")
                        .person(Person.builder()
                                .firstName("Anna")
                                .lastName("Petrov")
                                .build())
                        .build()
        );

        Recruiter savedRecruiter2 = recruiterRepository.saveAndFlush(
                Recruiter.builder()
                        .companyName("Microsoft")
                        .person(Person.builder()
                                .firstName("John")
                                .lastName("Smith")
                                .build())
                        .build()
        );

        Vacancy savedVacancy1 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Java Developer")
                        .salary(1000.0f)
                        .technologyStack(List.of("Java", "Spring", "Hibernate", "PostgreSQL", "SQL"))
                        .recruiter(savedRecruiter1)
                        .build()
        );

        Vacancy savedVacancy2 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Sql Developer")
                        .salary(4000.0f)
                        .technologyStack(List.of("SQL", "PostgreSQL", "MySQL"))
                        .recruiter(savedRecruiter1)
                        .build()
        );

        Vacancy savedVacancy3 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Java Developer")
                        .salary(3000.0f)
                        .technologyStack(List.of("Java", "Spring", "Hibernate", "PostgreSQL", "SQL"))
                        .recruiter(savedRecruiter2)
                        .build()
        );

        Vacancy savedVacancy4 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Sql Developer")
                        .salary(4000.0f)
                        .technologyStack(List.of("SQL", "PostgreSQL", "MySQL"))
                        .recruiter(savedRecruiter2)
                        .build()
        );

        Vacancy savedVacancy5 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Java Developer")
                        .salary(1000.0f)
                        .technologyStack(List.of("Java", "Spring", "Hibernate", "PostgreSQL", "SQL"))
                        .recruiter(savedRecruiter2)
                        .build()
        );

        String request = """
                {
                    "position": "Java Developer",
                    "min_salary": 2000.0,
                    "recruiter_id": %d,
                    "technology_stack": ["PostgreSQL", "SQL"]
                }
                """.formatted(savedRecruiter2.getId());

        assertThat(vacancyRepository.findAll()).hasSize(5);

        // When and then
        MockHttpServletResponse response = mockMvc.perform(post("/api/v1/vacancy/_report")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, startsWith("attachment; filename=vacancies_")))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, endsWith(".xlsx")))
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsByteArray()).isNotEmpty();

        byte[] contentAsByteArray = response.getContentAsByteArray();

        XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(contentAsByteArray));

        XSSFSheet vacancySheet = workbook.getSheetAt(0);
        assertThat(vacancySheet.getSheetName()).isEqualTo("Vacancies");
        assertThat(vacancySheet.getPhysicalNumberOfRows()).isEqualTo(2);

        assertThat(vacancySheet.getRow(0).getCell(0).getStringCellValue()).isEqualTo("Vacancy ID");
        assertThat(vacancySheet.getRow(0).getCell(1).getStringCellValue()).isEqualTo("Position");
        assertThat(vacancySheet.getRow(0).getCell(2).getStringCellValue()).isEqualTo("Salary");
        assertThat(vacancySheet.getRow(0).getCell(3).getStringCellValue()).isEqualTo("Technology Stack");
        assertThat(vacancySheet.getRow(0).getCell(4).getStringCellValue()).isEqualTo("Company Name");
        assertThat(vacancySheet.getRow(0).getCell(5).getStringCellValue()).isEqualTo("Created At");
        assertThat(vacancySheet.getRow(0).getCell(6).getStringCellValue()).isEqualTo("Recruiter_id");
        assertThat(vacancySheet.getRow(0).getCell(7).getStringCellValue()).isEqualTo("Recruiter First Name");
        assertThat(vacancySheet.getRow(0).getCell(8).getStringCellValue()).isEqualTo("Recruiter Last Name");

        assertThat(vacancySheet.getRow(1).getCell(0).getNumericCellValue()).isEqualTo(savedVacancy3.getId().doubleValue());
        assertThat(vacancySheet.getRow(1).getCell(1).getStringCellValue()).isEqualTo("Java Developer");
        assertThat(vacancySheet.getRow(1).getCell(2).getNumericCellValue()).isEqualTo(3000.0);
        assertThat(vacancySheet.getRow(1).getCell(3).getStringCellValue()).isEqualTo("Java, Spring, Hibernate, PostgreSQL, SQL");
        assertThat(vacancySheet.getRow(1).getCell(4).getStringCellValue()).isEqualTo(savedRecruiter2.getCompanyName());
        assertThat(vacancySheet.getRow(1).getCell(5).getStringCellValue()).isEqualTo(savedVacancy3.getCreatedAt().toString());
        assertThat(vacancySheet.getRow(1).getCell(6).getNumericCellValue()).isEqualTo(savedRecruiter2.getId().doubleValue());
        assertThat(vacancySheet.getRow(1).getCell(7).getStringCellValue()).isEqualTo(savedRecruiter2.getPerson().getFirstName());
        assertThat(vacancySheet.getRow(1).getCell(8).getStringCellValue()).isEqualTo(savedRecruiter2.getPerson().getLastName());
    }

    @Test
    void uploadVacanciesJsonFile_shouldReturnOk_whenFileIsValid() throws Exception {
        // Given
        Recruiter savedRecruiter = recruiterRepository.saveAndFlush(
                Recruiter.builder()
                        .companyName("Google")
                        .person(Person.builder()
                                .firstName("Anna")
                                .lastName("Petrov")
                                .build())
                        .build()
        );

        String json = """
                [
                    {
                        "position": "Java Developer",
                        "salary": 1000.0,
                        "technology_stack": ["Java", "Spring"],
                        "recruiter_id": %d
                    },
                    {
                        "position": "Python Developer",
                        "salary": 2000.0,
                        "technology_stack": ["Python", "Django"],
                        "recruiter_id": %d
                    }
                ]
                """.formatted(savedRecruiter.getId(), savedRecruiter.getId());

        MockMultipartFile file = new MockMultipartFile("file", "vacancies.json", "application/json", json.getBytes());

        // When and then
        mockMvc.perform(multipart("/api/v1/vacancy/upload")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("2 vacancies uploaded successfully"))
                .andExpect(jsonPath("$.success_uploaded").value(2))
                .andExpect(jsonPath("$.failed_uploaded").value(0));

        List<Vacancy> vacancies = new ArrayList<>(vacancyRepository.findAllByRecruiterId(savedRecruiter.getId()));

        assertThat(vacancies).hasSize(2);

        assertThat(vacancies.get(0).getPosition()).isEqualTo("Java Developer");
        assertThat(vacancies.get(0).getSalary()).isEqualTo(1000.0f);
        assertThat(vacancies.get(0).getTechnologyStack()).containsExactlyInAnyOrder("Java", "Spring");
        assertThat(vacancies.get(0).getRecruiter()).isEqualTo(savedRecruiter);

        assertThat(vacancies.get(1).getPosition()).isEqualTo("Python Developer");
        assertThat(vacancies.get(1).getSalary()).isEqualTo(2000.0f);
        assertThat(vacancies.get(1).getTechnologyStack()).containsExactlyInAnyOrder("Python", "Django");
        assertThat(vacancies.get(1).getRecruiter()).isEqualTo(savedRecruiter);
    }

    @Test
    void uploadVacanciesJsonFile_shouldReturnOk_whenFileIsEmpty() throws Exception {
        // Given
        List<Vacancy> vacanciesBeforeUploading = vacancyRepository.findAll();
        MockMultipartFile file = new MockMultipartFile("file", "vacancies.json", "application/json", new byte[0]);

        // When and then
        mockMvc.perform(multipart("/api/v1/vacancy/upload")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("No vacancies uploaded"))
                .andExpect(jsonPath("$.success_uploaded").value(0))
                .andExpect(jsonPath("$.failed_uploaded").value(0));

        assertThat(vacancyRepository.findAll()).isEqualTo(vacanciesBeforeUploading);
    }

    @Test
    void uploadVacanciesJsonFile_shouldReturnOk_whenJsonFileContainsTwoValidRecordAndTwoUnvalidatedRecord () throws Exception {
        // Given
        Recruiter savedRecruiter = recruiterRepository.saveAndFlush(
                Recruiter.builder()
                        .companyName("Google")
                        .person(Person.builder()
                                .firstName("Anna")
                                .lastName("Petrov")
                                .build())
                        .build()
        );

        String json = """
                [
                    {
                        "position": "Java Developer",
                        "technology_stack": ["Java", "Spring"],
                        "recruiter_id": %d
                    },
                    {
                        "salary": 2000.0,
                        "technology_stack": ["Python", "Django"],
                        "recruiter_id": %d
                    },
                    {
                        "position": "PhP Developer",
                        "salary": 1000.0,
                        "recruiter_id": %d
                    },
                    {
                        "position": "Python Developer",
                        "salary": 2000.0,
                        "technology_stack": ["Python", "Django"]
                    }
                ]
                """.formatted(savedRecruiter.getId(), savedRecruiter.getId(), savedRecruiter.getId());

        MockMultipartFile file = new MockMultipartFile("file", "vacancies.json", "application/json", json.getBytes());

        // When and then
        mockMvc.perform(multipart("/api/v1/vacancy/upload")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("2 vacancies uploaded successfully"))
                .andExpect(jsonPath("$.success_uploaded").value(2))
                .andExpect(jsonPath("$.failed_uploaded").value(2));

        List<Vacancy> vacancies = new ArrayList<>(vacancyRepository.findAllByRecruiterId(savedRecruiter.getId()));

        assertThat(vacancies).hasSize(2);

        assertThat(vacancies.get(0).getPosition()).isEqualTo("Java Developer");
        assertThat(vacancies.get(0).getSalary()).isNull();
        assertThat(vacancies.get(0).getTechnologyStack()).containsExactlyInAnyOrder("Java", "Spring");
        assertThat(vacancies.get(0).getRecruiter()).isEqualTo(savedRecruiter);

        assertThat(vacancies.get(1).getPosition()).isEqualTo("PhP Developer");
        assertThat(vacancies.get(1).getSalary()).isEqualTo(1000.0f);
        assertThat(vacancies.get(1).getTechnologyStack()).isEmpty();
        assertThat(vacancies.get(1).getRecruiter()).isEqualTo(savedRecruiter);
    }

    @Test
    void uploadVacanciesJsonFile_shouldReturnOk_whenJsonFileContainsOnlyInvalidRecord() throws Exception {
        // Given
        Recruiter savedRecruiter = recruiterRepository.saveAndFlush(
                Recruiter.builder()
                        .companyName("Google")
                        .person(Person.builder()
                                .firstName("Anna")
                                .lastName("Petrov")
                                .build())
                        .build()
        );

        Vacancy savedVacancy1 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Java Developer")
                        .salary(1000.0f)
                        .technologyStack(List.of("Java", "Spring"))
                        .recruiter(savedRecruiter)
                        .build()
        );

        Vacancy savedVacancy2 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Python Developer")
                        .salary(2000.0f)
                        .technologyStack(List.of("Python", "Django"))
                        .recruiter(savedRecruiter)
                        .build()
        );

        List<Vacancy> vacanciesBeforeUploading = vacancyRepository.findAll();

        assertThat(vacanciesBeforeUploading).hasSizeGreaterThanOrEqualTo(2);

        String json = """
                [
                    {
                        "position": "Java Developer",
                        "technology_stack": ["Java", "Spring"]
                    },
                    {
                        "salary": 2000.0,
                        "technology_stack": ["Python", "Django"]
                    },
                    {
                        "Invalid_Field": "PhP Developer",
                        "Invalid_Field_2": 1000.0
                    },
                    {
                        "salary": 2000.0,
                        "technology_stack": ["Python", "Django"],
                        "recruiter_id": 1
                    }
                ]
                """;

        MockMultipartFile file = new MockMultipartFile("file", "vacancies.json", "application/json", json.getBytes());

        // When and then
        mockMvc.perform(multipart("/api/v1/vacancy/upload")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("No vacancies uploaded"))
                .andExpect(jsonPath("$.success_uploaded").value(0))
                .andExpect(jsonPath("$.failed_uploaded").value(4));

        assertThat(vacancyRepository.findAll()).isEqualTo(vacanciesBeforeUploading);
    }

    @Test
    void uploadVacanciesJsonFile_shouldReturnOk_whenJsonContainsInvalidRecruiterId() throws Exception {
        // Given
        List<Vacancy> vacanciesBeforeUploading = vacancyRepository.findAll();
        String json = """
                [
                    {
                        "position": "Java Developer",
                        "salary": 1000.0,
                        "technology_stack": ["Java", "Spring"],
                        "recruiter_id": 1
                    },
                    {
                        "position": "Python Developer",
                        "salary": 2000.0,
                        "technology_stack": ["Python", "Django"],
                        "recruiter_id": 2
                    }
                ]
                """;

        MockMultipartFile file = new MockMultipartFile("file", "vacancies.json", "application/json", json.getBytes());

        // When and then
        mockMvc.perform(multipart("/api/v1/vacancy/upload")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("No vacancies uploaded"))
                .andExpect(jsonPath("$.success_uploaded").value(0))
                .andExpect(jsonPath("$.failed_uploaded").value(2));

        assertThat(vacancyRepository.findAll()).isEqualTo(vacanciesBeforeUploading);
    }

    @Test
    void uploadVacanciesJsonFile_shouldReturnOk_whenJsonContainsInvalidRecruiterIdAndValidRecruiterId() throws Exception {
        // Given
        Recruiter savedRecruiter = recruiterRepository.saveAndFlush(
                Recruiter.builder()
                        .companyName("Google")
                        .person(Person.builder()
                                .firstName("Anna")
                                .lastName("Petrov")
                                .build())
                        .build()
        );

        List<Vacancy> vacanciesBeforeUploading = vacancyRepository.findAll();
        String json = """
                [
                    {
                        "position": "Java Developer",
                        "salary": 1000.0,
                        "technology_stack": ["Java", "Spring"],
                        "recruiter_id": 1
                    },
                    {
                        "position": "Python Developer",
                        "salary": 2000.0,
                        "technology_stack": ["Python", "Django"],
                        "recruiter_id": %d
                    }
                ]
                """.formatted(savedRecruiter.getId());

        MockMultipartFile file = new MockMultipartFile("file", "vacancies.json", "application/json", json.getBytes());

        // When and then
        mockMvc.perform(multipart("/api/v1/vacancy/upload")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("1 vacancies uploaded successfully"))
                .andExpect(jsonPath("$.success_uploaded").value(1))
                .andExpect(jsonPath("$.failed_uploaded").value(1));

        List<Vacancy> vacancies = new ArrayList<>(vacancyRepository.findAllByRecruiterId(savedRecruiter.getId()));

        assertThat(vacancies).hasSize(1);

        assertThat(vacancies.get(0).getPosition()).isEqualTo("Python Developer");
        assertThat(vacancies.get(0).getSalary()).isEqualTo(2000.0f);
        assertThat(vacancies.get(0).getTechnologyStack()).containsExactlyInAnyOrder("Python", "Django");
        assertThat(vacancies.get(0).getRecruiter()).isEqualTo(savedRecruiter);

        assertThat(vacancyRepository.findAll()).hasSize(vacanciesBeforeUploading.size() + 1);
    }

    @Test
    void uploadVacanciesJsonFile_shouldReturnOk_whenJsonContainsInvalidSalary() throws Exception {
        // Given
        Recruiter savedRecruiter = recruiterRepository.saveAndFlush(
                Recruiter.builder()
                        .companyName("Google")
                        .person(Person.builder()
                                .firstName("Anna")
                                .lastName("Petrov")
                                .build())
                        .build()
        );

        List<Vacancy> vacanciesBeforeUploading = vacancyRepository.findAll();
        String json = """
                [
                    {
                        "position": "Java Developer",
                        "salary": -1000.0,
                        "technology_stack": ["Java", "Spring"],
                        "recruiter_id": %d
                    },
                    {
                        "position": "Python Developer",
                        "recruiter_id": %d
                    }
                ]
                """.formatted(savedRecruiter.getId(), savedRecruiter.getId());

        MockMultipartFile file = new MockMultipartFile("file", "vacancies.json", "application/json", json.getBytes());

        // When and then
        mockMvc.perform(multipart("/api/v1/vacancy/upload")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("1 vacancies uploaded successfully"))
                .andExpect(jsonPath("$.success_uploaded").value(1))
                .andExpect(jsonPath("$.failed_uploaded").value(1));

        List<Vacancy> vacancies = new ArrayList<>(vacancyRepository.findAllByRecruiterId(savedRecruiter.getId()));

        assertThat(vacancies).hasSize(1);

        assertThat(vacancies.get(0).getPosition()).isEqualTo("Python Developer");
        assertThat(vacancies.get(0).getRecruiter()).isEqualTo(savedRecruiter);

        assertThat(vacancyRepository.findAll()).hasSize(vacanciesBeforeUploading.size() + 1);
    }

    @Test
    void generateReportExcel_shouldReturnOkAndAllVacancies_whenFilterIsEmpty() throws Exception {
        // Given
        Recruiter savedRecruiter1 = recruiterRepository.saveAndFlush(
                Recruiter.builder()
                        .companyName("Google")
                        .person(Person.builder()
                                .firstName("Anna")
                                .lastName("Petrov")
                                .build())
                        .build()
        );

        Recruiter savedRecruiter2 = recruiterRepository.saveAndFlush(
                Recruiter.builder()
                        .companyName("Microsoft")
                        .person(Person.builder()
                                .firstName("John")
                                .lastName("Smith")
                                .build())
                        .build()
        );

        Vacancy savedVacancy1 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Java Developer")
                        .salary(1000.0f)
                        .technologyStack(List.of("Java", "Spring", "Hibernate", "PostgreSQL", "SQL"))
                        .recruiter(savedRecruiter1)
                        .build()
        );

        Vacancy savedVacancy2 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Sql Developer")
                        .salary(4000.0f)
                        .technologyStack(List.of("SQL", "PostgreSQL", "MySQL"))
                        .recruiter(savedRecruiter1)
                        .build()
        );

        Vacancy savedVacancy3 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Java Developer")
                        .salary(3000.0f)
                        .technologyStack(List.of("Java", "Spring", "Hibernate", "PostgreSQL", "SQL"))
                        .recruiter(savedRecruiter2)
                        .build()
        );

        Vacancy savedVacancy4 = vacancyRepository.saveAndFlush(
                Vacancy.builder()
                        .position("Sql Developer")
                        .salary(4000.0f)
                        .technologyStack(List.of("SQL", "PostgreSQL", "MySQL"))
                        .recruiter(savedRecruiter2)
                        .build()
        );


        assertThat(vacancyRepository.findAll()).hasSize(4);

        // When and then
        MockHttpServletResponse response = mockMvc.perform(post("/api/v1/vacancy/_report")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, startsWith("attachment; filename=vacancies_")))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, endsWith(".xlsx")))
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsByteArray()).isNotEmpty();

        byte[] contentAsByteArray = response.getContentAsByteArray();
        XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(contentAsByteArray));

        assertThat(workbook.getNumberOfSheets()).isEqualTo(1);
        XSSFSheet vacancySheet = workbook.getSheetAt(0);
        assertThat(vacancySheet.getSheetName()).isEqualTo("Vacancies");
        assertThat(vacancySheet.getPhysicalNumberOfRows()).isEqualTo(5);

        assertThat(vacancySheet.getRow(0).getCell(0).getStringCellValue()).isEqualTo("Vacancy ID");
        assertThat(vacancySheet.getRow(0).getCell(1).getStringCellValue()).isEqualTo("Position");
        assertThat(vacancySheet.getRow(0).getCell(2).getStringCellValue()).isEqualTo("Salary");
        assertThat(vacancySheet.getRow(0).getCell(3).getStringCellValue()).isEqualTo("Technology Stack");
        assertThat(vacancySheet.getRow(0).getCell(4).getStringCellValue()).isEqualTo("Company Name");
        assertThat(vacancySheet.getRow(0).getCell(5).getStringCellValue()).isEqualTo("Created At");
        assertThat(vacancySheet.getRow(0).getCell(6).getStringCellValue()).isEqualTo("Recruiter_id");
        assertThat(vacancySheet.getRow(0).getCell(7).getStringCellValue()).isEqualTo("Recruiter First Name");
        assertThat(vacancySheet.getRow(0).getCell(8).getStringCellValue()).isEqualTo("Recruiter Last Name");

        assertThat(vacancySheet.getRow(1).getCell(0).getNumericCellValue()).isEqualTo(savedVacancy1.getId().doubleValue());
        assertThat(vacancySheet.getRow(1).getCell(1).getStringCellValue()).isEqualTo(savedVacancy1.getPosition());
        assertThat(vacancySheet.getRow(1).getCell(2).getNumericCellValue()).isEqualTo(savedVacancy1.getSalary().doubleValue());
        assertThat(vacancySheet.getRow(1).getCell(3).getStringCellValue()).isEqualTo("Java, Spring, Hibernate, PostgreSQL, SQL");
        assertThat(vacancySheet.getRow(1).getCell(4).getStringCellValue()).isEqualTo(savedRecruiter1.getCompanyName());
        assertThat(vacancySheet.getRow(1).getCell(5).getStringCellValue()).isEqualTo(savedVacancy1.getCreatedAt().toString());
        assertThat(vacancySheet.getRow(1).getCell(6).getNumericCellValue()).isEqualTo(savedRecruiter1.getId().doubleValue());
        assertThat(vacancySheet.getRow(1).getCell(7).getStringCellValue()).isEqualTo(savedRecruiter1.getPerson().getFirstName());
        assertThat(vacancySheet.getRow(1).getCell(8).getStringCellValue()).isEqualTo(savedRecruiter1.getPerson().getLastName());

        assertThat(vacancySheet.getRow(2).getCell(0).getNumericCellValue()).isEqualTo(savedVacancy2.getId().doubleValue());
        assertThat(vacancySheet.getRow(2).getCell(1).getStringCellValue()).isEqualTo(savedVacancy2.getPosition());
        assertThat(vacancySheet.getRow(2).getCell(2).getNumericCellValue()).isEqualTo(savedVacancy2.getSalary().doubleValue());
        assertThat(vacancySheet.getRow(2).getCell(3).getStringCellValue()).isEqualTo("SQL, PostgreSQL, MySQL");
        assertThat(vacancySheet.getRow(2).getCell(4).getStringCellValue()).isEqualTo(savedRecruiter1.getCompanyName());
        assertThat(vacancySheet.getRow(2).getCell(5).getStringCellValue()).isEqualTo(savedVacancy2.getCreatedAt().toString());
        assertThat(vacancySheet.getRow(2).getCell(6).getNumericCellValue()).isEqualTo(savedRecruiter1.getId().doubleValue());
        assertThat(vacancySheet.getRow(2).getCell(7).getStringCellValue()).isEqualTo(savedRecruiter1.getPerson().getFirstName());
        assertThat(vacancySheet.getRow(2).getCell(8).getStringCellValue()).isEqualTo(savedRecruiter1.getPerson().getLastName());

        assertThat(vacancySheet.getRow(3).getCell(0).getNumericCellValue()).isEqualTo(savedVacancy3.getId().doubleValue());
        assertThat(vacancySheet.getRow(3).getCell(1).getStringCellValue()).isEqualTo(savedVacancy3.getPosition());
        assertThat(vacancySheet.getRow(3).getCell(2).getNumericCellValue()).isEqualTo(savedVacancy3.getSalary().doubleValue());
        assertThat(vacancySheet.getRow(3).getCell(3).getStringCellValue()).isEqualTo("Java, Spring, Hibernate, PostgreSQL, SQL");
        assertThat(vacancySheet.getRow(3).getCell(4).getStringCellValue()).isEqualTo(savedRecruiter2.getCompanyName());
        assertThat(vacancySheet.getRow(3).getCell(5).getStringCellValue()).isEqualTo(savedVacancy3.getCreatedAt().toString());
        assertThat(vacancySheet.getRow(3).getCell(6).getNumericCellValue()).isEqualTo(savedRecruiter2.getId().doubleValue());
        assertThat(vacancySheet.getRow(3).getCell(7).getStringCellValue()).isEqualTo(savedRecruiter2.getPerson().getFirstName());
        assertThat(vacancySheet.getRow(3).getCell(8).getStringCellValue()).isEqualTo(savedRecruiter2.getPerson().getLastName());

        assertThat(vacancySheet.getRow(4).getCell(0).getNumericCellValue()).isEqualTo(savedVacancy4.getId().doubleValue());
        assertThat(vacancySheet.getRow(4).getCell(1).getStringCellValue()).isEqualTo(savedVacancy4.getPosition());
        assertThat(vacancySheet.getRow(4).getCell(2).getNumericCellValue()).isEqualTo(savedVacancy4.getSalary().doubleValue());
        assertThat(vacancySheet.getRow(4).getCell(3).getStringCellValue()).isEqualTo("SQL, PostgreSQL, MySQL");
        assertThat(vacancySheet.getRow(4).getCell(4).getStringCellValue()).isEqualTo(savedRecruiter2.getCompanyName());
        assertThat(vacancySheet.getRow(4).getCell(5).getStringCellValue()).isEqualTo(savedVacancy4.getCreatedAt().toString());
        assertThat(vacancySheet.getRow(4).getCell(6).getNumericCellValue()).isEqualTo(savedRecruiter2.getId().doubleValue());
        assertThat(vacancySheet.getRow(4).getCell(7).getStringCellValue()).isEqualTo(savedRecruiter2.getPerson().getFirstName());
        assertThat(vacancySheet.getRow(4).getCell(8).getStringCellValue()).isEqualTo(savedRecruiter2.getPerson().getLastName());
    }
}