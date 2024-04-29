package org.prof.it.soft.parser;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class VacancyJsonParserTest {

    @Test
    void parseVacancies_whenEmptyJson() throws IOException {
        // Given
        String json = "{}";
        VacancyJsonParser vacancyJsonParser = new VacancyJsonParser();

        // When
        var vacancies = vacancyJsonParser.parseVacancies(json);

        // Then
        assertTrue(vacancies.isEmpty());
    }

    @Test
    void parseVacancies_arrayJson_whenJsonContainsOneValidRecord() throws IOException {
        // Given
        String json = """
                [
                    {
                      "position": "Java Developer",
                      "salary": 1000.0,
                      "technology_stack": ["Java", "Spring"],
                      "recruiter_id": 1
                    }
                ]
                """;

        VacancyJsonParser vacancyJsonParser = new VacancyJsonParser();

        // When
        var vacancies = vacancyJsonParser.parseVacancies(json);

        // Then
        assertEquals(1, vacancies.size());
        assertEquals("Java Developer", vacancies.get(0).getPosition());
        assertEquals(1000.0F, vacancies.get(0).getSalary());
        assertEquals(2, vacancies.get(0).getTechnologyStack().size());
        assertEquals("Java", vacancies.get(0).getTechnologyStack().get(0));
        assertEquals("Spring", vacancies.get(0).getTechnologyStack().get(1));
        assertEquals(1, vacancies.get(0).getRecruiterId());
    }

    @Test
    void parseVacancies_arrayJson_whenJsonContainsOneInvalidRecord() throws IOException {
        // Given
        String json = """
                [
                    {
                      "position": "Java Developer",
                      "salary": 1000.0,
                      "technology_stack": ["Java", "Spring"],
                      "recruiter_id": 1,
                      "invalid_field": "invalid"
                    }
                ]
                """;

        VacancyJsonParser vacancyJsonParser = new VacancyJsonParser();

        // When
        var vacancies = vacancyJsonParser.parseVacancies(json);

        // Then
        assertTrue(vacancies.isEmpty());
    }

    @Test
    void parseVacancies_arrayJson_whenJsonContainsOneRecordWithoutRecruiterIdRequiredField() throws IOException {
        // Given
        String json = """
                [
                    {
                      "position": "Java Developer",
                      "salary": 1000.0,
                      "technology_stack": ["Java", "Spring"]
                    }
                ]
                """;

        VacancyJsonParser vacancyJsonParser = new VacancyJsonParser();

        // When
        var vacancies = vacancyJsonParser.parseVacancies(json);

        // Then
        assertTrue(vacancies.isEmpty());
    }

    @Test
    void parseVacancies_arrayJson_whenJsonContainsOneRecordWithoutPositionRequiredField() throws IOException {
        // Given
        String json = """
                [
                    {
                      "salary": 1000.0,
                      "technology_stack": ["Java", "Spring"],
                      "recruiter_id": 1
                    }
                ]
                """;

        VacancyJsonParser vacancyJsonParser = new VacancyJsonParser();

        // When
        var vacancies = vacancyJsonParser.parseVacancies(json);

        // Then
        assertTrue(vacancies.isEmpty());
    }

    @Test
    void parseVacancies_arrayJson_whenJsonContainsThreeValidRecords() throws Exception {
        // Given
        String json = """
                [
                    {
                      "position": "Java Developer",
                      "salary": 1000.0,
                      "technology_stack": ["Java", "Spring"],
                      "recruiter_id": 1
                    },
                    {
                      "position": "SQL Developer",
                      "salary": 3000.0,
                      "technology_stack": ["PostgreSQL", "SQL"],
                      "recruiter_id": 5
                    },
                    {
                      "position": "Ruby Developer",
                      "salary": 9000.0,
                      "technology_stack": ["Ruby", "Rails", "RSpec"],
                      "recruiter_id": 6
                    }
                ]
                """;

        VacancyJsonParser vacancyJsonParser = new VacancyJsonParser();

        // When
        var vacancies = vacancyJsonParser.parseVacancies(json);

        // Then
        assertEquals(3, vacancies.size());

        assertThat(vacancies.get(0).getPosition()).isEqualTo("Java Developer");
        assertThat(vacancies.get(0).getSalary()).isEqualTo(1000.0F);
        assertThat(vacancies.get(0).getTechnologyStack()).containsExactly("Java", "Spring");
        assertThat(vacancies.get(0).getRecruiterId()).isEqualTo(1);

        assertThat(vacancies.get(1).getPosition()).isEqualTo("SQL Developer");
        assertThat(vacancies.get(1).getSalary()).isEqualTo(3000.0F);
        assertThat(vacancies.get(1).getTechnologyStack()).containsExactly("PostgreSQL", "SQL");
        assertThat(vacancies.get(1).getRecruiterId()).isEqualTo(5);

        assertThat(vacancies.get(2).getPosition()).isEqualTo("Ruby Developer");
        assertThat(vacancies.get(2).getSalary()).isEqualTo(9000.0F);
        assertThat(vacancies.get(2).getTechnologyStack()).containsExactly("Ruby", "Rails", "RSpec");
        assertThat(vacancies.get(2).getRecruiterId()).isEqualTo(6);
    }

    @Test
    void parseVacancies_arrayJson_whenJsonContainsOneRecordWithNegativeSalary() throws IOException {
        // Given
        String json = """
                [
                    {
                      "position": "Java Developer",
                      "salary": -1000.0,
                      "technology_stack": ["Java", "Spring"],
                      "recruiter_id": 1
                    }
                ]
                """;

        VacancyJsonParser vacancyJsonParser = new VacancyJsonParser();

        // When
        var vacancies = vacancyJsonParser.parseVacancies(json);

        // Then
        assertTrue(vacancies.isEmpty());
    }

    @Test
    void parseVacancies_arrayJson_whenJsonContainsOneRecordWithZeroSalary() throws IOException {
        // Given
        String json = """
                [
                    {
                      "position": "Java Developer",
                      "salary": 0.0,
                      "technology_stack": ["Java", "Spring"],
                      "recruiter_id": 1
                    }
                ]
                """;

        VacancyJsonParser vacancyJsonParser = new VacancyJsonParser();

        // When
        var vacancies = vacancyJsonParser.parseVacancies(json);

        // Then
        assertTrue(vacancies.isEmpty());
    }

    @Test
    void parseVacancies_arrayJson_whenJsonContainsThreeValidAndOneUnvalidRecords() throws Exception {
        // Given
        String json = """
                [
                    {
                      "position": "Java Developer",
                      "salary": 1000.0,
                      "technology_stack": ["Java", "Spring"],
                      "recruiter_id": 1
                    },
                    {
                      "salary": 110.0,
                      "technology_stack": ["Python", "Django"]
                    },
                    {
                      "position": "SQL Developer",
                      "salary": 3000.0,
                      "technology_stack": ["PostgreSQL", "SQL"],
                      "recruiter_id": 5
                    },
                    {
                      "position": "Ruby Developer",
                      "salary": 9000.0,
                      "technology_stack": ["Ruby", "Rails", "RSpec"],
                      "recruiter_id": 6
                    }
                ]
                """;

        VacancyJsonParser vacancyJsonParser = new VacancyJsonParser();

        // When
        var vacancies = vacancyJsonParser.parseVacancies(json);

        // Then
        assertEquals(3, vacancies.size());

        assertThat(vacancies.get(0).getPosition()).isEqualTo("Java Developer");
        assertThat(vacancies.get(0).getSalary()).isEqualTo(1000.0F);
        assertThat(vacancies.get(0).getTechnologyStack()).containsExactly("Java", "Spring");
        assertThat(vacancies.get(0).getRecruiterId()).isEqualTo(1);

        assertThat(vacancies.get(1).getPosition()).isEqualTo("SQL Developer");
        assertThat(vacancies.get(1).getSalary()).isEqualTo(3000.0F);
        assertThat(vacancies.get(1).getTechnologyStack()).containsExactly("PostgreSQL", "SQL");
        assertThat(vacancies.get(1).getRecruiterId()).isEqualTo(5);

        assertThat(vacancies.get(2).getPosition()).isEqualTo("Ruby Developer");
        assertThat(vacancies.get(2).getSalary()).isEqualTo(9000.0F);
        assertThat(vacancies.get(2).getTechnologyStack()).containsExactly("Ruby", "Rails", "RSpec");
        assertThat(vacancies.get(2).getRecruiterId()).isEqualTo(6);
    }

    @Test
    void parseVacancies_arrayJson_whenJsonContainsOneRecordWithEmptyTechnologyStack() throws IOException {
        // Given
        String json = """
                [
                    {
                      "position": "Java Developer",
                      "salary": 1000.0,
                      "technology_stack": [],
                      "recruiter_id": 1
                    }
                ]
                """;

        VacancyJsonParser vacancyJsonParser = new VacancyJsonParser();

        // When
        var vacancies = vacancyJsonParser.parseVacancies(json);

        // Then
        assertThat(vacancies.get(0).getPosition()).isEqualTo("Java Developer");
        assertThat(vacancies.get(0).getSalary()).isEqualTo(1000.0F);
        assertThat(vacancies.get(0).getTechnologyStack()).isEmpty();
        assertThat(vacancies.get(0).getRecruiterId()).isEqualTo(1);
    }

    @Test
    void parseVacancies_arrayJson_whenJsonContainsOneRecordWithNullTechnologyStack() throws IOException {
        // Given
        String json = """
                [
                    {
                      "position": "Java Developer",
                      "salary": 1000.0,
                      "technology_stack": null,
                      "recruiter_id": 1
                    }
                ]
                """;

        VacancyJsonParser vacancyJsonParser = new VacancyJsonParser();

        // When
        var vacancies = vacancyJsonParser.parseVacancies(json);

        // Then
        assertThat(vacancies).hasSize(1);

        assertThat(vacancies.get(0).getPosition()).isEqualTo("Java Developer");
        assertThat(vacancies.get(0).getSalary()).isEqualTo(1000.0F);
        assertNull(vacancies.get(0).getTechnologyStack());
        assertThat(vacancies.get(0).getRecruiterId()).isEqualTo(1);
    }

    @Test
    void parseVacancies_arrayJson_whenJsonContainsOneRecordWithNullSalary() throws IOException {
        // Given
        String json = """
                [
                    {
                      "position": "Java Developer",
                      "salary": null,
                      "technology_stack": ["Java", "Spring"],
                      "recruiter_id": 1
                    }
                ]
                """;

        VacancyJsonParser vacancyJsonParser = new VacancyJsonParser();

        // When
        var vacancies = vacancyJsonParser.parseVacancies(json);

        // Then
        assertThat(vacancies).hasSize(1);

        assertThat(vacancies.get(0).getPosition()).isEqualTo("Java Developer");
        assertNull(vacancies.get(0).getSalary());
        assertThat(vacancies.get(0).getTechnologyStack()).containsExactly("Java", "Spring");
        assertThat(vacancies.get(0).getRecruiterId()).isEqualTo(1);
    }

    @Test
    void parseVacancies_arrayJson_whenJsonContainsOneRecordWithNullPositionAndTwoValidRecords() throws IOException {
        // Given
        String json = """
                [
                    {
                      "position": null,
                      "salary": 1000.0,
                      "technology_stack": ["Java", "Spring"],
                      "recruiter_id": 1
                    },
                    {
                      "position": "SQL Developer",
                      "salary": 3000.0,
                      "technology_stack": ["PostgreSQL", "SQL"],
                      "recruiter_id": 5
                    },
                    {
                      "position": "Ruby Developer",
                      "salary": 9000.0,
                      "technology_stack": ["Ruby", "Rails", "RSpec"],
                      "recruiter_id": 6
                    }
                ]
                """;

        VacancyJsonParser vacancyJsonParser = new VacancyJsonParser();

        // When
        var vacancies = vacancyJsonParser.parseVacancies(json);

        // Then
        assertThat(vacancies).hasSize(2);

        assertThat(vacancies.get(0).getPosition()).isEqualTo("SQL Developer");
        assertThat(vacancies.get(0).getSalary()).isEqualTo(3000.0F);
        assertThat(vacancies.get(0).getTechnologyStack()).containsExactly("PostgreSQL", "SQL");
        assertThat(vacancies.get(0).getRecruiterId()).isEqualTo(5);

        assertThat(vacancies.get(1).getPosition()).isEqualTo("Ruby Developer");
        assertThat(vacancies.get(1).getSalary()).isEqualTo(9000.0F);
        assertThat(vacancies.get(1).getTechnologyStack()).containsExactly("Ruby", "Rails", "RSpec");
        assertThat(vacancies.get(1).getRecruiterId()).isEqualTo(6);
    }

    @Test
    void parseVacancies_arrayJsonAndAllowedRecruiterIds_whenJsonContainsOneValidRecord() throws IOException {
        // Given
        String json = """
                [
                    {
                      "position": "Java Developer",
                      "salary": 1000.0,
                      "technology_stack": ["Java", "Spring"],
                      "recruiter_id": 1
                    }
                ]
                """;

        VacancyJsonParser vacancyJsonParser = new VacancyJsonParser();

        // When
        var vacancies = vacancyJsonParser.parseVacancies(json, Set.of(1L));

        // Then
        assertEquals(1, vacancies.size());
        assertEquals("Java Developer", vacancies.get(0).getPosition());
        assertEquals(1000.0F, vacancies.get(0).getSalary());
        assertEquals(2, vacancies.get(0).getTechnologyStack().size());
        assertEquals("Java", vacancies.get(0).getTechnologyStack().get(0));
        assertEquals("Spring", vacancies.get(0).getTechnologyStack().get(1));
        assertEquals(1, vacancies.get(0).getRecruiterId());
    }

    @Test
    void parseVacancies_arrayJsonAndAllowedRecruiterIds_whenJsonContainsOneInvalidRecord() throws IOException {
        // Given
        String json = """
                [
                    {
                      "position": "Java Developer",
                      "salary": 1000.0,
                      "technology_stack": ["Java", "Spring"],
                      "recruiter_id": 1
                    }
                ]
                """;

        VacancyJsonParser vacancyJsonParser = new VacancyJsonParser();

        // When
        var vacancies = vacancyJsonParser.parseVacancies(json, Set.of(2L));

        // Then
        assertTrue(vacancies.isEmpty());
    }

    @Test
    void parseVacancies_arrayJsonAndAllowedRecruiterIds_whenJsonContainsOneValidAndOneInvalidRecord() throws IOException {
        // Given
        String json = """
                [
                    {
                      "position": "Java Developer",
                      "salary": 1000.0,
                      "technology_stack": ["Java", "Spring"],
                      "recruiter_id": 1
                    },
                    {
                      "position": "SQL Developer",
                      "salary": 3000.0,
                      "technology_stack": ["PostgreSQL", "SQL"],
                      "recruiter_id": 2
                    }
                ]
                """;

        VacancyJsonParser vacancyJsonParser = new VacancyJsonParser();

        // When
        var vacancies = vacancyJsonParser.parseVacancies(json, Set.of(1L));

        // Then
        assertEquals(1, vacancies.size());
        assertEquals("Java Developer", vacancies.get(0).getPosition());
        assertEquals(1000.0F, vacancies.get(0).getSalary());
        assertEquals(2, vacancies.get(0).getTechnologyStack().size());
        assertEquals("Java", vacancies.get(0).getTechnologyStack().get(0));
        assertEquals("Spring", vacancies.get(0).getTechnologyStack().get(1));
        assertEquals(1, vacancies.get(0).getRecruiterId());
    }

    @Test
    void parseVacancies_arrayJsonAndAllowedRecruiterIds_whenJsonContainsOneRecordWithoutRecruiterIdRequiredField() throws IOException {
        // Given
        String json = """
                [
                    {
                      "position": "Java Developer",
                      "salary": 1000.0,
                      "technology_stack": ["Java", "Spring"]
                    }
                ]
                """;

        VacancyJsonParser vacancyJsonParser = new VacancyJsonParser();

        // When
        var vacancies = vacancyJsonParser.parseVacancies(json, Set.of(1L));

        // Then
        assertTrue(vacancies.isEmpty());
    }

    @Test
    void parseVacancies__arrayJsonAndAllowedRecruiterIds_whenJsonContainsTwoRecordsWithDifferentRecruiterIds() throws IOException {
        // Given
        String json = """
                [
                    {
                      "position": "Java Developer",
                      "salary": 1000.0,
                      "technology_stack": ["Java", "Spring"],
                      "recruiter_id": 1
                    },
                    {
                      "position": "SQL Developer",
                      "salary": 3000.0,
                      "technology_stack": ["PostgreSQL", "SQL"],
                      "recruiter_id": 2
                    }
                ]
                """;

        VacancyJsonParser vacancyJsonParser = new VacancyJsonParser();

        // When
        var vacancies = vacancyJsonParser.parseVacancies(json, Set.of(1L, 2L));

        // Then
        assertEquals(2, vacancies.size());

        assertThat(vacancies.get(0).getPosition()).isEqualTo("Java Developer");
        assertThat(vacancies.get(0).getSalary()).isEqualTo(1000.0F);
        assertThat(vacancies.get(0).getTechnologyStack()).containsExactly("Java", "Spring");
        assertThat(vacancies.get(0).getRecruiterId()).isEqualTo(1);

        assertThat(vacancies.get(1).getPosition()).isEqualTo("SQL Developer");
        assertThat(vacancies.get(1).getSalary()).isEqualTo(3000.0F);
        assertThat(vacancies.get(1).getTechnologyStack()).containsExactly("PostgreSQL", "SQL");
        assertThat(vacancies.get(1).getRecruiterId()).isEqualTo(2);
    }

    @Test
    void parseVacancies_arrayJsonAndAllowedRecruiterIds_whenJsonContainsOneRecordWithNullPositionAndOneValidRecord() throws IOException {
        // Given
        String json = """
                [
                    {
                      "position": null,
                      "salary": 1000.0,
                      "technology_stack": ["Java", "Spring"],
                      "recruiter_id": 1
                    },
                    {
                      "position": "SQL Developer",
                      "salary": 3000.0,
                      "technology_stack": ["PostgreSQL", "SQL"],
                      "recruiter_id": 2
                    }
                ]
                """;

        VacancyJsonParser vacancyJsonParser = new VacancyJsonParser();

        // When
        var vacancies = vacancyJsonParser.parseVacancies(json, Set.of(1L, 2L));

        // Then
        assertThat(vacancies).hasSize(1);

        assertThat(vacancies.get(0).getPosition()).isEqualTo("SQL Developer");
        assertThat(vacancies.get(0).getSalary()).isEqualTo(3000.0F);
        assertThat(vacancies.get(0).getTechnologyStack()).containsExactly("PostgreSQL", "SQL");
        assertThat(vacancies.get(0).getRecruiterId()).isEqualTo(2);
    }

    @Test
    void parseVacancies_arrayJsonAndAllowedRecruiterIds_whenJsonContainsRecruiterIdsIsNull() throws IOException {
        // Given
        String json = """
                [
                    {
                      "position": "Java Developer",
                      "salary": 1000.0,
                      "technology_stack": ["Java", "Spring"],
                      "recruiter_id": null
                    },
                    {
                      "position": "SQL Developer",
                      "recruiter_id": 2
                    }
                ]
                """;

        VacancyJsonParser vacancyJsonParser = new VacancyJsonParser();

        // When
        HashSet<Long> allowedRecruiterIds = new HashSet<>();
        allowedRecruiterIds.add(1L);
        allowedRecruiterIds.add(2L);
        allowedRecruiterIds.add(null);
        var vacancies = vacancyJsonParser.parseVacancies(json, allowedRecruiterIds);

        // Then
        assertThat(vacancies).hasSize(1);

        assertThat(vacancies.get(0).getPosition()).isEqualTo("SQL Developer");
        assertThat(vacancies.get(0).getSalary()).isNull();
        assertThat(vacancies.get(0).getTechnologyStack()).isNull();
        assertThat(vacancies.get(0).getRecruiterId()).isEqualTo(2);
    }

    @Test
    void parseVacancies_arrayJsonAndAllowedRecruiterIds_whenJsonContainsRecruiterIdsIsNullAndAllowedRecruiterIdsIsNull() throws IOException {
        // Given
        String json = """
                [
                    {
                      "position": "Java Developer",
                      "salary": 1000.0,
                      "technology_stack": ["Java", "Spring"],
                      "recruiter_id": null
                    },
                    {
                      "position": "SQL Developer",
                      "recruiter_id": 2
                    },
                    {
                      "position": "Ruby Developer",
                      "recruiter_id": "FIVE"
                    }
                ]
                """;

        VacancyJsonParser vacancyJsonParser = new VacancyJsonParser();

        // When
        var vacancies = vacancyJsonParser.parseVacancies(json, null);

        // Then
        assertThat(vacancies).hasSize(1);

        assertThat(vacancies.get(0).getPosition()).isEqualTo("SQL Developer");
        assertThat(vacancies.get(0).getSalary()).isNull();
        assertThat(vacancies.get(0).getTechnologyStack()).isNull();
        assertThat(vacancies.get(0).getRecruiterId()).isEqualTo(2);
    }
}