package org.prof.it.soft.service;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.prof.it.soft.dto.RecruiterDto;
import org.prof.it.soft.dto.VacancyDto;
import org.prof.it.soft.dto.stats.PositionVacancyStatsDto;
import org.prof.it.soft.dto.stats.RecruiterVacancyStatsDto;
import org.prof.it.soft.dto.stats.SalaryVacancyStatsDto;
import org.prof.it.soft.dto.stats.TechnologyVacancyStatsDto;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DtoSerializerTest {


    protected static DtoSerializer dtoSerializer;

    @BeforeAll
    public static void beforeAll() {
        ObjectMapper mapperJson = new ObjectMapper();
        XmlMapper mapperXml = new XmlMapper();

        mapperJson.enable(SerializationFeature.INDENT_OUTPUT);

        mapperXml.enable(SerializationFeature.INDENT_OUTPUT);
        mapperXml.enable(ToXmlGenerator.Feature.WRITE_XML_DECLARATION);

        dtoSerializer = new DtoSerializer(mapperJson, mapperXml);
    }


    @Test
    void objectToJson_serializesAllFieldsToValidJson() throws Exception {
        RecruiterDto recruiterDto = RecruiterDto.builder()
                .firstName("Vladyslav")
                .lastName("Bondar")
                .companyName("ProfITsoft")
                .build();

        VacancyDto vacancyDto = VacancyDto.builder()
                .recruiterDto(recruiterDto)
                .position("Full-Stack developer")
                .salary(1000F)
                .technologyStack(List.of("Java", "Spring", "React"))
                .build();


        String recruiterJson = dtoSerializer.objectToJson(vacancyDto);

        assertThatJson(recruiterJson).inPath("$.position").isEqualTo("Full-Stack developer");
        assertThatJson(recruiterJson).inPath("$.salary").isEqualTo(1000F);
        assertThatJson(recruiterJson).inPath("$.technology_stack").isEqualTo("Java, Spring, React");
        assertThatJson(recruiterJson).inPath("$.recruiter_first_name").isEqualTo("Vladyslav");
        assertThatJson(recruiterJson).inPath("$.recruiter_last_name").isEqualTo("Bondar");
        assertThatJson(recruiterJson).inPath("$.recruiter_company_name").isEqualTo("ProfITsoft");
    }

    @Test
    void objectToJson_omitsSalaryFieldWhenAbsentInDto() throws Exception {
        RecruiterDto recruiterDto = RecruiterDto.builder()
                .firstName("Vladyslav")
                .lastName("Bondar")
                .companyName("ProfITsoft")
                .build();

        VacancyDto vacancyDto = VacancyDto.builder()
                .recruiterDto(recruiterDto)
                .position("Full-Stack developer")
                .technologyStack(List.of("Java", "Spring", "React"))
                .build();


        String recruiterJson = dtoSerializer.objectToJson(vacancyDto);

        assertThatJson(recruiterJson).inPath("$.position").isEqualTo("Full-Stack developer");
        assertThatJson(recruiterJson).inPath("$.salary").isAbsent();
        assertThatJson(recruiterJson).inPath("$.technology_stack").isEqualTo("Java, Spring, React");
        assertThatJson(recruiterJson).inPath("$.recruiter_first_name").isEqualTo("Vladyslav");
        assertThatJson(recruiterJson).inPath("$.recruiter_last_name").isEqualTo("Bondar");
        assertThatJson(recruiterJson).inPath("$.recruiter_company_name").isEqualTo("ProfITsoft");
    }

    @Test
    void objectToJson_omitsTechnologyStackFieldWhenNullInDto() throws Exception {
        RecruiterDto recruiterDto = RecruiterDto.builder()
                .firstName("Vladyslav")
                .lastName("Bondar")
                .companyName("ProfITsoft")
                .build();

        VacancyDto vacancyDto = VacancyDto.builder()
                .recruiterDto(recruiterDto)
                .salary(1000F)
                .technologyStack(null)
                .position("Full-Stack developer")
                .build();


        String recruiterJson = dtoSerializer.objectToJson(vacancyDto);

        assertThatJson(recruiterJson).inPath("$.position").isEqualTo("Full-Stack developer");
        assertThatJson(recruiterJson).inPath("$.salary").isEqualTo(1000F);
        assertThatJson(recruiterJson).inPath("$.technology_stack").isAbsent();
        assertThatJson(recruiterJson).inPath("$.recruiter_first_name").isEqualTo("Vladyslav");
        assertThatJson(recruiterJson).inPath("$.recruiter_last_name").isEqualTo("Bondar");
        assertThatJson(recruiterJson).inPath("$.recruiter_company_name").isEqualTo("ProfITsoft");
    }

    @Test
    void objectToJson_omitsTechnologyStackFieldWhenEmptyInDto() throws Exception {
        RecruiterDto recruiterDto = RecruiterDto.builder()
                .firstName("Vladyslav")
                .lastName("Bondar")
                .companyName("ProfITsoft")
                .build();

        VacancyDto vacancyDto = VacancyDto.builder()
                .recruiterDto(recruiterDto)
                .salary(1000F)
                .technologyStack(List.of())
                .position("Full-Stack developer")
                .build();


        String recruiterJson = dtoSerializer.objectToJson(vacancyDto);

        assertThatJson(recruiterJson).inPath("$.position").isEqualTo("Full-Stack developer");
        assertThatJson(recruiterJson).inPath("$.salary").isEqualTo(1000F);
        assertThatJson(recruiterJson).inPath("$.technology_stack").isAbsent();
        assertThatJson(recruiterJson).inPath("$.recruiter_first_name").isEqualTo("Vladyslav");
        assertThatJson(recruiterJson).inPath("$.recruiter_last_name").isEqualTo("Bondar");
        assertThatJson(recruiterJson).inPath("$.recruiter_company_name").isEqualTo("ProfITsoft");
    }

    @Test
    void objectToJson_throwsNullPointerException_whenVacancyDtoIsNull() {
        assertThrows(NullPointerException.class, () -> dtoSerializer.objectToJson(null));
    }

    @Test
    void jsonToVacancyDto_correctlyDeserializesFullVacancyDto() throws Exception {
        String json = """
                {
                  "position" : "Full-Stack developer",
                  "salary" : 1000.0,
                  "technology_stack" : "Java, Spring, React",
                  "recruiter_first_name" : "Vladyslav",
                  "recruiter_last_name" : "Bondar",
                  "recruiter_company_name" : "ProfITsoft"
                }
                """;

        VacancyDto vacancyDto = dtoSerializer.jsonToVacancyDto(json);

        assertThat(vacancyDto.getPosition()).isEqualTo("Full-Stack developer");
        assertThat(vacancyDto.getSalary()).isEqualTo(1000F);
        assertThat(vacancyDto.getTechnologyStack()).isEqualTo((List.of("Java", "Spring", "React")));
        assertThat(vacancyDto.getRecruiterDto().getFirstName()).isEqualTo("Vladyslav");
        assertThat(vacancyDto.getRecruiterDto().getLastName()).isEqualTo("Bondar");
        assertThat(vacancyDto.getRecruiterDto().getCompanyName()).isEqualTo("ProfITsoft");
    }

    @Test
    void jsonToVacancyDtoListObjects_correctlyDeserializesMultipleVacancies() throws Exception {
        String json = """
                [
                  {
                    "position": "Full-Stack developer",
                    "salary": 1000.0,
                    "technology_stack": "Java, Spring, React",
                    "recruiter_first_name": "Vladyslav",
                    "recruiter_last_name": "Bondar",
                    "recruiter_company_name": "ProfITsoft"
                  },
                  {
                    "position": "Data Scientist",
                    "salary": 1200.0,
                    "technology_stack": "Python, Machine Learning",
                    "recruiter_first_name": "Олена",
                    "recruiter_last_name": "Петрова",
                    "recruiter_company_name": "DataTech"
                  },
                  {
                    "position": "Junior QA Engineer",
                    "salary": 800.0,
                    "technology_stack": "Selenium, Cypress",
                    "recruiter_first_name": "Іван",
                    "recruiter_last_name": "Франко",
                    "recruiter_company_name": "SoftServe"
                  },
                  {
                    "position": "Frontend Developer",
                    "salary": 900.0,
                    "technology_stack": "React, Angular, Vue.js",
                    "recruiter_first_name": "Марія",
                    "recruiter_last_name": "Степаненко",
                    "recruiter_company_name": "EPAM"
                  }
                ]
                """;

        List<VacancyDto> vacancyDtos = dtoSerializer.jsonToVacancyDtoList(json);

        assertThat(vacancyDtos).hasSize(4);

        assertThat(vacancyDtos.get(0).getPosition()).isEqualTo("Full-Stack developer");
        assertThat(vacancyDtos.get(0).getSalary()).isEqualTo(1000F);
        assertThat(vacancyDtos.get(0).getTechnologyStack()).isEqualTo((List.of("Java", "Spring", "React")));
        assertThat(vacancyDtos.get(0).getRecruiterDto().getFirstName()).isEqualTo("Vladyslav");
        assertThat(vacancyDtos.get(0).getRecruiterDto().getLastName()).isEqualTo("Bondar");
        assertThat(vacancyDtos.get(0).getRecruiterDto().getCompanyName()).isEqualTo("ProfITsoft");

        assertThat(vacancyDtos.get(1).getPosition()).isEqualTo("Data Scientist");
        assertThat(vacancyDtos.get(1).getSalary()).isEqualTo(1200F);
        assertThat(vacancyDtos.get(1).getTechnologyStack()).isEqualTo((List.of("Python", "Machine Learning")));
        assertThat(vacancyDtos.get(1).getRecruiterDto().getFirstName()).isEqualTo("Олена");
        assertThat(vacancyDtos.get(1).getRecruiterDto().getLastName()).isEqualTo("Петрова");
        assertThat(vacancyDtos.get(1).getRecruiterDto().getCompanyName()).isEqualTo("DataTech");

        assertThat(vacancyDtos.get(2).getPosition()).isEqualTo("Junior QA Engineer");
        assertThat(vacancyDtos.get(2).getSalary()).isEqualTo(800F);
        assertThat(vacancyDtos.get(2).getTechnologyStack()).isEqualTo((List.of("Selenium", "Cypress")));
        assertThat(vacancyDtos.get(2).getRecruiterDto().getFirstName()).isEqualTo("Іван");
        assertThat(vacancyDtos.get(2).getRecruiterDto().getLastName()).isEqualTo("Франко");
        assertThat(vacancyDtos.get(2).getRecruiterDto().getCompanyName()).isEqualTo("SoftServe");

        assertThat(vacancyDtos.get(3).getPosition()).isEqualTo("Frontend Developer");
        assertThat(vacancyDtos.get(3).getSalary()).isEqualTo(900F);
        assertThat(vacancyDtos.get(3).getTechnologyStack()).isEqualTo((List.of("React", "Angular", "Vue.js")));
        assertThat(vacancyDtos.get(3).getRecruiterDto().getFirstName()).isEqualTo("Марія");
        assertThat(vacancyDtos.get(3).getRecruiterDto().getLastName()).isEqualTo("Степаненко");
        assertThat(vacancyDtos.get(3).getRecruiterDto().getCompanyName()).isEqualTo("EPAM");
    }

    @Test
    void jsonToObject_correctlyDeserializesVacancy_whenSalaryAbsentInJson() throws Exception {
        String json = """
                {
                  "position" : "Full-Stack developer",
                  "technology_stack" : "Java, Spring, React",
                  "recruiter_first_name" : "Vladyslav",
                  "recruiter_last_name" : "Bondar",
                  "recruiter_company_name" : "ProfITsoft"
                }
                """;

        VacancyDto vacancyDto = dtoSerializer.jsonToVacancyDto(json);

        assertThat(vacancyDto.getPosition()).isEqualTo("Full-Stack developer");
        assertThat(vacancyDto.getSalary()).isNull();
        assertThat(vacancyDto.getTechnologyStack()).isEqualTo((List.of("Java", "Spring", "React")));
        assertThat(vacancyDto.getRecruiterDto().getFirstName()).isEqualTo("Vladyslav");
        assertThat(vacancyDto.getRecruiterDto().getLastName()).isEqualTo("Bondar");
        assertThat(vacancyDto.getRecruiterDto().getCompanyName()).isEqualTo("ProfITsoft");
    }

    @Test
    void jsonToVacancyDtoListObjects_correctlyDeserializesVacancies_includingSomeWithoutSalary() throws Exception {
        String json = """
                [
                  {
                    "position": "Full-Stack developer",
                    "technology_stack": "Java, Spring, React",
                    "recruiter_first_name": "Vladyslav",
                    "recruiter_last_name": "Bondar",
                    "recruiter_company_name": "ProfITsoft"
                  },
                  {
                    "position": "Data Scientist",
                    "technology_stack": "Python, Machine Learning",
                    "recruiter_first_name": "Олена",
                    "recruiter_last_name": "Петрова",
                    "recruiter_company_name": "DataTech"
                  },
                  {
                    "position": "Junior QA Engineer",
                    "salary": 800.0,
                    "technology_stack": "Selenium, Cypress",
                    "recruiter_first_name": "Іван",
                    "recruiter_last_name": "Франко",
                    "recruiter_company_name": "SoftServe"
                  },
                  {
                    "position": "Frontend Developer",
                    "technology_stack": "React, Angular, Vue.js",
                    "recruiter_first_name": "Марія",
                    "recruiter_last_name": "Степаненко",
                    "recruiter_company_name": "EPAM"
                  }
                ]
                """;

        List<VacancyDto> vacancyDtos = dtoSerializer.jsonToVacancyDtoList(json);

        assertThat(vacancyDtos).hasSize(4);

        assertThat(vacancyDtos.get(0).getPosition()).isEqualTo("Full-Stack developer");
        assertThat(vacancyDtos.get(0).getSalary()).isNull();
        assertThat(vacancyDtos.get(0).getTechnologyStack()).isEqualTo((List.of("Java", "Spring", "React")));
        assertThat(vacancyDtos.get(0).getRecruiterDto().getFirstName()).isEqualTo("Vladyslav");
        assertThat(vacancyDtos.get(0).getRecruiterDto().getLastName()).isEqualTo("Bondar");
        assertThat(vacancyDtos.get(0).getRecruiterDto().getCompanyName()).isEqualTo("ProfITsoft");

        assertThat(vacancyDtos.get(1).getPosition()).isEqualTo("Data Scientist");
        assertThat(vacancyDtos.get(1).getSalary()).isNull();
        assertThat(vacancyDtos.get(1).getTechnologyStack()).isEqualTo((List.of("Python", "Machine Learning")));
        assertThat(vacancyDtos.get(1).getRecruiterDto().getFirstName()).isEqualTo("Олена");
        assertThat(vacancyDtos.get(1).getRecruiterDto().getLastName()).isEqualTo("Петрова");
        assertThat(vacancyDtos.get(1).getRecruiterDto().getCompanyName()).isEqualTo("DataTech");

        assertThat(vacancyDtos.get(2).getPosition()).isEqualTo("Junior QA Engineer");
        assertThat(vacancyDtos.get(2).getSalary()).isEqualTo(800F);
        assertThat(vacancyDtos.get(2).getTechnologyStack()).isEqualTo((List.of("Selenium", "Cypress")));
        assertThat(vacancyDtos.get(2).getRecruiterDto().getFirstName()).isEqualTo("Іван");
        assertThat(vacancyDtos.get(2).getRecruiterDto().getLastName()).isEqualTo("Франко");
        assertThat(vacancyDtos.get(2).getRecruiterDto().getCompanyName()).isEqualTo("SoftServe");

        assertThat(vacancyDtos.get(3).getPosition()).isEqualTo("Frontend Developer");
        assertThat(vacancyDtos.get(3).getSalary()).isNull();
        assertThat(vacancyDtos.get(3).getTechnologyStack()).isEqualTo((List.of("React", "Angular", "Vue.js")));
        assertThat(vacancyDtos.get(3).getRecruiterDto().getFirstName()).isEqualTo("Марія");
        assertThat(vacancyDtos.get(3).getRecruiterDto().getLastName()).isEqualTo("Степаненко");
        assertThat(vacancyDtos.get(3).getRecruiterDto().getCompanyName()).isEqualTo("EPAM");
    }

    @Test
    void jsonToObject_correctlyDeserializesVacancy_whenTechnologyStackAbsentInJson() throws Exception {
        String json = """
                {
                  "position" : "Full-Stack developer",
                  "salary" : 1000.0,
                  "recruiter_first_name" : "Vladyslav",
                  "recruiter_last_name" : "Bondar",
                  "recruiter_company_name" : "ProfITsoft"
                }
                """;

        VacancyDto vacancyDto = dtoSerializer.jsonToVacancyDto(json);

        assertThat(vacancyDto.getPosition()).isEqualTo("Full-Stack developer");
        assertThat(vacancyDto.getSalary()).isEqualTo(1000F);
        assertThat(vacancyDto.getTechnologyStack()).isEqualTo(null);
        assertThat(vacancyDto.getRecruiterDto().getFirstName()).isEqualTo("Vladyslav");
        assertThat(vacancyDto.getRecruiterDto().getLastName()).isEqualTo("Bondar");
        assertThat(vacancyDto.getRecruiterDto().getCompanyName()).isEqualTo("ProfITsoft");
    }

    @Test
    void jsonToVacancyDtoListObjects_correctlyDeserializesVacancies_includingSomeWithoutTechStack() throws Exception {
        String json = """
                [
                  {
                    "position": "Full-Stack developer",
                    "salary": 1000.0,
                    "recruiter_first_name": "Vladyslav",
                    "recruiter_last_name": "Bondar",
                    "recruiter_company_name": "ProfITsoft"
                  },
                  {
                    "position": "Data Scientist",
                    "salary": 1200.0,
                    "technology_stack": "Python, Machine Learning",
                    "recruiter_first_name": "Олена",
                    "recruiter_last_name": "Петрова",
                    "recruiter_company_name": "DataTech"
                  },
                  {
                    "position": "Junior QA Engineer",
                    "salary": 800.0,
                    "recruiter_first_name": "Іван",
                    "recruiter_last_name": "Франко",
                    "recruiter_company_name": "SoftServe"
                  },
                  {
                    "position": "Frontend Developer",
                    "salary": 900.0,
                    "recruiter_first_name": "Марія",
                    "recruiter_last_name": "Степаненко",
                    "recruiter_company_name": "EPAM"
                  }
                ]
                """;

        List<VacancyDto> vacancyDtos = dtoSerializer.jsonToVacancyDtoList(json);

        assertThat(vacancyDtos).hasSize(4);

        assertThat(vacancyDtos.get(0).getPosition()).isEqualTo("Full-Stack developer");
        assertThat(vacancyDtos.get(0).getSalary()).isEqualTo(1000F);
        assertThat(vacancyDtos.get(0).getTechnologyStack()).isNull();
        assertThat(vacancyDtos.get(0).getRecruiterDto().getFirstName()).isEqualTo("Vladyslav");
        assertThat(vacancyDtos.get(0).getRecruiterDto().getLastName()).isEqualTo("Bondar");
        assertThat(vacancyDtos.get(0).getRecruiterDto().getCompanyName()).isEqualTo("ProfITsoft");

        assertThat(vacancyDtos.get(1).getPosition()).isEqualTo("Data Scientist");
        assertThat(vacancyDtos.get(1).getSalary()).isEqualTo(1200F);
        assertThat(vacancyDtos.get(1).getTechnologyStack()).isEqualTo((List.of("Python", "Machine Learning")));
        assertThat(vacancyDtos.get(1).getRecruiterDto().getFirstName()).isEqualTo("Олена");
        assertThat(vacancyDtos.get(1).getRecruiterDto().getLastName()).isEqualTo("Петрова");
        assertThat(vacancyDtos.get(1).getRecruiterDto().getCompanyName()).isEqualTo("DataTech");

        assertThat(vacancyDtos.get(2).getPosition()).isEqualTo("Junior QA Engineer");
        assertThat(vacancyDtos.get(2).getSalary()).isEqualTo(800F);
        assertThat(vacancyDtos.get(2).getTechnologyStack()).isNull();
        assertThat(vacancyDtos.get(2).getRecruiterDto().getFirstName()).isEqualTo("Іван");
        assertThat(vacancyDtos.get(2).getRecruiterDto().getLastName()).isEqualTo("Франко");
        assertThat(vacancyDtos.get(2).getRecruiterDto().getCompanyName()).isEqualTo("SoftServe");

        assertThat(vacancyDtos.get(3).getPosition()).isEqualTo("Frontend Developer");
        assertThat(vacancyDtos.get(3).getSalary()).isEqualTo(900F);
        assertThat(vacancyDtos.get(3).getTechnologyStack()).isNull();
        assertThat(vacancyDtos.get(3).getRecruiterDto().getFirstName()).isEqualTo("Марія");
        assertThat(vacancyDtos.get(3).getRecruiterDto().getLastName()).isEqualTo("Степаненко");
        assertThat(vacancyDtos.get(3).getRecruiterDto().getCompanyName()).isEqualTo("EPAM");
    }

    @Test
    void jsonToObject_throwsJsonMappingException_whenPositionMissingInJson() {
        String json = """
                {
                  "salary" : 1000.0,
                  "technology_stack" : "Java, Spring, React",
                  "recruiter_first_name" : "Vladyslav",
                  "recruiter_last_name" : "Bondar",
                  "recruiter_company_name" : "ProfITsoft"
                }
                """;

        Assertions.assertThrows(JsonMappingException.class, () -> dtoSerializer.jsonToVacancyDto(json));
    }

    @Test
    void jsonToListObjects_throwsJsonMappingException_whenVacancyDtoListContainsVacancyWithoutPosition() {
        String json = """
                [
                  {
                    "position": "Full-Stack developer",
                    "salary": 1000.0,
                    "technology_stack": "Java, Spring, React",
                    "recruiter_first_name": "Vladyslav",
                    "recruiter_last_name": "Bondar",
                    "recruiter_company_name": "ProfITsoft"
                  },
                  {
                    "salary": 1200.0,
                    "technology_stack": "Python, Machine Learning",
                    "recruiter_first_name": "Олена",
                    "recruiter_last_name": "Петрова",
                    "recruiter_company_name": "DataTech"
                  },
                  {
                    "salary": 800.0,
                    "technology_stack": "Selenium, Cypress",
                    "recruiter_first_name": "Іван",
                    "recruiter_last_name": "Франко",
                    "recruiter_company_name": "SoftServe"
                  },
                  {
                    "position": "Frontend Developer",
                    "salary": 900.0,
                    "technology_stack": "React, Angular, Vue.js",
                    "recruiter_first_name": "Марія",
                    "recruiter_last_name": "Степаненко",
                    "recruiter_company_name": "EPAM"
                  }
                ]
                """;

        Assertions.assertThrows(JsonMappingException.class, () -> dtoSerializer.jsonToVacancyDtoList(json));
    }

    @Test
    void jsonToObject_correctlyDeserializesVacancy_whenRecruiterCompanyAbsentInJson() throws Exception {
        String json = """
                {
                  "position" : "Full-Stack developer",
                  "salary" : 1000.0,
                  "technology_stack" : "Java, Spring, React",
                  "recruiter_first_name" : "Vladyslav",
                  "recruiter_last_name" : "Bondar"
                }
                """;

        VacancyDto vacancyDto = dtoSerializer.jsonToVacancyDto(json);

        assertThat(vacancyDto.getPosition()).isEqualTo("Full-Stack developer");
        assertThat(vacancyDto.getSalary()).isEqualTo(1000F);
        assertThat(vacancyDto.getTechnologyStack()).isEqualTo((List.of("Java", "Spring", "React")));
        assertThat(vacancyDto.getRecruiterDto().getFirstName()).isEqualTo("Vladyslav");
        assertThat(vacancyDto.getRecruiterDto().getLastName()).isEqualTo("Bondar");
        assertThat(vacancyDto.getRecruiterDto().getCompanyName()).isNull();
    }

    @Test
    void jsonToVacancyDtoListObjects_correctlyDeserializesVacancies_includingSomeWithoutRecruiterCompany() throws Exception {
        String json = """
                [
                  {
                    "position": "Full-Stack developer",
                    "salary": 1000.0,
                    "technology_stack": "Java, Spring, React",
                    "recruiter_first_name": "Vladyslav",
                    "recruiter_last_name": "Bondar"
                  },
                  {
                    "position": "Data Scientist",
                    "salary": 1200.0,
                    "technology_stack": "Python, Machine Learning",
                    "recruiter_first_name": "Олена",
                    "recruiter_last_name": "Петрова",
                    "recruiter_company_name": "DataTech"
                  },
                  {
                    "position": "Junior QA Engineer",
                    "salary": 800.0,
                    "technology_stack": "Selenium, Cypress",
                    "recruiter_first_name": "Іван",
                    "recruiter_last_name": "Франко"
                  },
                  {
                    "position": "Frontend Developer",
                    "salary": 900.0,
                    "technology_stack": "React, Angular, Vue.js",
                    "recruiter_first_name": "Марія",
                    "recruiter_last_name": "Степаненко"
                  }
                ]
                """;

        List<VacancyDto> vacancyDtos = dtoSerializer.jsonToVacancyDtoList(json);

        assertThat(vacancyDtos).hasSize(4);

        assertThat(vacancyDtos.get(0).getPosition()).isEqualTo("Full-Stack developer");
        assertThat(vacancyDtos.get(0).getSalary()).isEqualTo(1000F);
        assertThat(vacancyDtos.get(0).getTechnologyStack()).isEqualTo((List.of("Java", "Spring", "React")));
        assertThat(vacancyDtos.get(0).getRecruiterDto().getFirstName()).isEqualTo("Vladyslav");
        assertThat(vacancyDtos.get(0).getRecruiterDto().getLastName()).isEqualTo("Bondar");
        assertThat(vacancyDtos.get(0).getRecruiterDto().getCompanyName()).isNull();

        assertThat(vacancyDtos.get(1).getPosition()).isEqualTo("Data Scientist");
        assertThat(vacancyDtos.get(1).getSalary()).isEqualTo(1200F);
        assertThat(vacancyDtos.get(1).getTechnologyStack()).isEqualTo((List.of("Python", "Machine Learning")));
        assertThat(vacancyDtos.get(1).getRecruiterDto().getFirstName()).isEqualTo("Олена");
        assertThat(vacancyDtos.get(1).getRecruiterDto().getLastName()).isEqualTo("Петрова");
        assertThat(vacancyDtos.get(1).getRecruiterDto().getCompanyName()).isEqualTo("DataTech");

        assertThat(vacancyDtos.get(2).getPosition()).isEqualTo("Junior QA Engineer");
        assertThat(vacancyDtos.get(2).getSalary()).isEqualTo(800F);
        assertThat(vacancyDtos.get(2).getTechnologyStack()).isEqualTo((List.of("Selenium", "Cypress")));
        assertThat(vacancyDtos.get(2).getRecruiterDto().getFirstName()).isEqualTo("Іван");
        assertThat(vacancyDtos.get(2).getRecruiterDto().getLastName()).isEqualTo("Франко");
        assertThat(vacancyDtos.get(2).getRecruiterDto().getCompanyName()).isNull();

        assertThat(vacancyDtos.get(3).getPosition()).isEqualTo("Frontend Developer");
        assertThat(vacancyDtos.get(3).getSalary()).isEqualTo(900F);
        assertThat(vacancyDtos.get(3).getTechnologyStack()).isEqualTo((List.of("React", "Angular", "Vue.js")));
        assertThat(vacancyDtos.get(3).getRecruiterDto().getFirstName()).isEqualTo("Марія");
        assertThat(vacancyDtos.get(3).getRecruiterDto().getLastName()).isEqualTo("Степаненко");
        assertThat(vacancyDtos.get(3).getRecruiterDto().getCompanyName()).isNull();
    }

    @Test
    void jsonToObject_correctlyDeserializesVacancy_whenRecruiterLastNameAbsentInJson() throws Exception {
        String json = """
                {
                  "position" : "Full-Stack developer",
                  "salary" : 1000.0,
                  "technology_stack" : "Java, Spring, React",
                  "recruiter_first_name" : "Vladyslav",
                  "recruiter_company_name" : "ProfITsoft"
                }
                """;

        VacancyDto vacancyDto = dtoSerializer.jsonToVacancyDto(json);

        assertThat(vacancyDto.getPosition()).isEqualTo("Full-Stack developer");
        assertThat(vacancyDto.getSalary()).isEqualTo(1000F);
        assertThat(vacancyDto.getTechnologyStack()).isEqualTo((List.of("Java", "Spring", "React")));
        assertThat(vacancyDto.getRecruiterDto().getFirstName()).isEqualTo("Vladyslav");
        assertThat(vacancyDto.getRecruiterDto().getLastName()).isNull();
        assertThat(vacancyDto.getRecruiterDto().getCompanyName()).isEqualTo("ProfITsoft");
    }

    @Test
    void jsonToVacancyDtoListObjects_deserialize_vacancy_without_recruiter_last_name() throws Exception {
        String json = """
                [
                  {
                    "position": "Full-Stack developer",
                    "salary": 1000.0,
                    "technology_stack": "Java, Spring, React",
                    "recruiter_first_name": "Vladyslav",
                    "recruiter_company_name": "ProfITsoft"
                  },
                  {
                    "position": "Data Scientist",
                    "salary": 1200.0,
                    "technology_stack": "Python, Machine Learning",
                    "recruiter_first_name": "Олена",
                    "recruiter_company_name": "DataTech"
                  },
                  {
                    "position": "Junior QA Engineer",
                    "salary": 800.0,
                    "technology_stack": "Selenium, Cypress",
                    "recruiter_first_name": "Іван",
                    "recruiter_last_name": "Франко",
                    "recruiter_company_name": "SoftServe"
                  },
                  {
                    "position": "Frontend Developer",
                    "salary": 900.0,
                    "technology_stack": "React, Angular, Vue.js",
                    "recruiter_first_name": "Марія",
                    "recruiter_company_name": "EPAM"
                  }
                ]
                """;

        List<VacancyDto> vacancyDtos = dtoSerializer.jsonToVacancyDtoList(json);

        assertThat(vacancyDtos).hasSize(4);

        assertThat(vacancyDtos.get(0).getPosition()).isEqualTo("Full-Stack developer");
        assertThat(vacancyDtos.get(0).getSalary()).isEqualTo(1000F);
        assertThat(vacancyDtos.get(0).getTechnologyStack()).isEqualTo((List.of("Java", "Spring", "React")));
        assertThat(vacancyDtos.get(0).getRecruiterDto().getFirstName()).isEqualTo("Vladyslav");
        assertThat(vacancyDtos.get(0).getRecruiterDto().getLastName()).isNull();
        assertThat(vacancyDtos.get(0).getRecruiterDto().getCompanyName()).isEqualTo("ProfITsoft");

        assertThat(vacancyDtos.get(1).getPosition()).isEqualTo("Data Scientist");
        assertThat(vacancyDtos.get(1).getSalary()).isEqualTo(1200F);
        assertThat(vacancyDtos.get(1).getTechnologyStack()).isEqualTo((List.of("Python", "Machine Learning")));
        assertThat(vacancyDtos.get(1).getRecruiterDto().getFirstName()).isEqualTo("Олена");
        assertThat(vacancyDtos.get(1).getRecruiterDto().getLastName()).isNull();
        assertThat(vacancyDtos.get(1).getRecruiterDto().getCompanyName()).isEqualTo("DataTech");

        assertThat(vacancyDtos.get(2).getPosition()).isEqualTo("Junior QA Engineer");
        assertThat(vacancyDtos.get(2).getSalary()).isEqualTo(800F);
        assertThat(vacancyDtos.get(2).getTechnologyStack()).isEqualTo((List.of("Selenium", "Cypress")));
        assertThat(vacancyDtos.get(2).getRecruiterDto().getFirstName()).isEqualTo("Іван");
        assertThat(vacancyDtos.get(2).getRecruiterDto().getLastName()).isEqualTo("Франко");
        assertThat(vacancyDtos.get(2).getRecruiterDto().getCompanyName()).isEqualTo("SoftServe");

        assertThat(vacancyDtos.get(3).getPosition()).isEqualTo("Frontend Developer");
        assertThat(vacancyDtos.get(3).getSalary()).isEqualTo(900F);
        assertThat(vacancyDtos.get(3).getTechnologyStack()).isEqualTo((List.of("React", "Angular", "Vue.js")));
        assertThat(vacancyDtos.get(3).getRecruiterDto().getFirstName()).isEqualTo("Марія");
        assertThat(vacancyDtos.get(3).getRecruiterDto().getLastName()).isNull();
        assertThat(vacancyDtos.get(3).getRecruiterDto().getCompanyName()).isEqualTo("EPAM");
    }

    @Test
    void jsonToObject_throwsJsonMappingException_whenRecruiterFirstNameMissingInJson() {
        String json = """
                {
                  "position" : "Full-Stack developer",
                  "salary" : 1000.0,
                  "technology_stack" : "Java, Spring, React",
                  "recruiter_last_name" : "Bondar",
                  "recruiter_company_name" : "ProfITsoft"
                }
                """;

        assertThrows(JsonMappingException.class, () -> dtoSerializer.jsonToVacancyDto(json));
    }

    @Test
    void jsonToListObjects_throwsJsonMappingException_whenVacancyDtoListContainsVacancyWithoutRecruiterFirstName() {
        String json = """
                [
                  {
                    "position": "Full-Stack developer",
                    "salary": 1000.0,
                    "technology_stack": "Java, Spring, React",
                    "recruiter_last_name": "Bondar",
                    "recruiter_company_name": "ProfITsoft"
                  },
                  {
                    "position": "Data Scientist",
                    "salary": 1200.0,
                    "technology_stack": "Python, Machine Learning",
                    "recruiter_first_name": "Олена",
                    "recruiter_last_name": "Петрова",
                    "recruiter_company_name": "DataTech"
                  },
                  {
                    "position": "Junior QA Engineer",
                    "salary": 800.0,
                    "technology_stack": "Selenium, Cypress",
                    "recruiter_first_name": "Іван",
                    "recruiter_last_name": "Франко",
                    "recruiter_company_name": "SoftServe"
                  },
                  {
                    "position": "Frontend Developer",
                    "salary": 900.0,
                    "technology_stack": "React, Angular, Vue.js",
                    "recruiter_last_name": "Степаненко",
                    "recruiter_company_name": "EPAM"
                  }
                ]
                """;

        assertThrows(JsonMappingException.class, () -> dtoSerializer.jsonToVacancyDtoList(json));
    }

    @Test
    void jsonToVacancyDto_deserialize_vacancy_without_any_info_about_recruiter() {
        String json = """
                {
                  "position" : "Full-Stack developer",
                  "salary" : 1000.0,
                  "technology_stack" : "Java, Spring, React"
                }
                """;

        assertThrows(JsonMappingException.class, () -> dtoSerializer.jsonToVacancyDto(json));
    }

    @Test
    void jsonToObject_throwsJsonMappingException_whenRecruiterInfoMissingInJson() {
        String json = """
                [
                  {
                    "position": "Full-Stack developer",
                    "salary": 1000.0,
                    "technology_stack": "Java, Spring, React",
                    "recruiter_first_name": "Vladyslav",
                    "recruiter_last_name": "Bondar",
                    "recruiter_company_name": "ProfITsoft"
                  },
                  {
                    "position": "Data Scientist",
                    "salary": 1200.0,
                    "technology_stack": "Python, Machine Learning",
                  },
                  {
                    "position": "Junior QA Engineer",
                    "salary": 800.0,
                    "technology_stack": "Selenium, Cypress",
                    "recruiter_first_name": "Іван",
                    "recruiter_last_name": "Франко",
                    "recruiter_company_name": "SoftServe"
                  },
                  {
                    "position": "Frontend Developer",
                    "salary": 900.0,
                    "technology_stack": "React, Angular, Vue.js",
                  }
                ]
                """;

        assertThrows(JsonMappingException.class, () -> dtoSerializer.jsonToVacancyDtoList(json));
    }

    @Test
    void jsonToObject_throwsJsonMappingException_whenEmptyJsonProvided() {
        String json = """
                {
                }
                """;

        assertThrows(JsonMappingException.class, () -> dtoSerializer.jsonToVacancyDto(json));
    }

    @Test
    void jsonToListObjects_returnsEmptyList_whenEmptyJsonProvidedVacancyDto() throws Exception {
        String json = """
                [
                ]
                """;

        List<VacancyDto> vacancyDto = dtoSerializer.jsonToVacancyDtoList(json);

        assertThat(vacancyDto).isEmpty();
    }


    @Test
    void jsonToVacancyDto_correctlyDeserializesVacancy_and_storesUnknownProperties() throws Exception {
        String json = """
                {
                  "fix_me": "fix this unknown_property",
                  "position" : "Full-Stack developer",
                  "salary" : 1000.0,
                  "fix_me-2" : "fix this unknown_property 2",
                  "technology_stack" : "Java, Spring, React",
                  "recruiter_first_name" : "Vladyslav",
                  "recruiter_last_name" : "Bondar",
                  "fix_me_3" : "fix this unknown_property 3",
                  "recruiter_company_name": "ProfITsoft",
                  "fix_me_4" : "fix this unknown_property 4"
                }
                """;

        VacancyDto vacancyDto = dtoSerializer.jsonToVacancyDto(json);

        assertThat(vacancyDto.getUnknownProperties()).containsEntry("fix_me", "fix this unknown_property");
        assertThat(vacancyDto.getUnknownProperties()).containsEntry("fix_me-2", "fix this unknown_property 2");
        assertThat(vacancyDto.getUnknownProperties()).containsEntry("fix_me_3", "fix this unknown_property 3");
        assertThat(vacancyDto.getUnknownProperties()).containsEntry("fix_me_4", "fix this unknown_property 4");
    }

    @Test
    void jsonFileToVacancyDtoList_correctlyDeserializesMultipleVacancies() throws Exception {
        File jsonFile = new File("src/test/resources/json/vacancy/vacancies_correctlyDeserializesMultipleVacancies.json");

        List<VacancyDto> vacancyDtos = dtoSerializer.jsonFileToVacancyDtoList(jsonFile.getAbsoluteFile());

        assertThat(vacancyDtos).hasSize(4);

        assertThat(vacancyDtos.get(0).getPosition()).isEqualTo("Full-Stack developer");
        assertThat(vacancyDtos.get(0).getSalary()).isEqualTo(1000F);
        assertThat(vacancyDtos.get(0).getTechnologyStack()).isEqualTo((List.of("Java", "Spring", "React")));
        assertThat(vacancyDtos.get(0).getRecruiterDto().getFirstName()).isEqualTo("Vladyslav");
        assertThat(vacancyDtos.get(0).getRecruiterDto().getLastName()).isEqualTo("Bondar");
        assertThat(vacancyDtos.get(0).getRecruiterDto().getCompanyName()).isEqualTo("ProfITsoft");

        assertThat(vacancyDtos.get(1).getPosition()).isEqualTo("Data Scientist");
        assertThat(vacancyDtos.get(1).getSalary()).isEqualTo(1200F);
        assertThat(vacancyDtos.get(1).getTechnologyStack()).isEqualTo((List.of("Python", "Machine Learning")));
        assertThat(vacancyDtos.get(1).getRecruiterDto().getFirstName()).isEqualTo("Олена");
        assertThat(vacancyDtos.get(1).getRecruiterDto().getLastName()).isEqualTo("Петрова");
        assertThat(vacancyDtos.get(1).getRecruiterDto().getCompanyName()).isEqualTo("DataTech");

        assertThat(vacancyDtos.get(2).getPosition()).isEqualTo("Junior QA Engineer");
        assertThat(vacancyDtos.get(2).getSalary()).isEqualTo(800F);
        assertThat(vacancyDtos.get(2).getTechnologyStack()).isEqualTo((List.of("Selenium", "Cypress")));
        assertThat(vacancyDtos.get(2).getRecruiterDto().getFirstName()).isEqualTo("Іван");
        assertThat(vacancyDtos.get(2).getRecruiterDto().getLastName()).isEqualTo("Франко");
        assertThat(vacancyDtos.get(2).getRecruiterDto().getCompanyName()).isEqualTo("SoftServe");

        assertThat(vacancyDtos.get(3).getPosition()).isEqualTo("Frontend Developer");
        assertThat(vacancyDtos.get(3).getSalary()).isEqualTo(900F);
        assertThat(vacancyDtos.get(3).getTechnologyStack()).isEqualTo((List.of("React", "Angular", "Vue.js")));
        assertThat(vacancyDtos.get(3).getRecruiterDto().getFirstName()).isEqualTo("Марія");
        assertThat(vacancyDtos.get(3).getRecruiterDto().getLastName()).isEqualTo("Степаненко");
        assertThat(vacancyDtos.get(3).getRecruiterDto().getCompanyName()).isEqualTo("EPAM");
    }

    @Test
    void jsonFileToVacancyDtoList_correctlyDeserializesVacancies_includingSomeWithoutRecruiterCompany() throws Exception {
        File jsonFile = new File("src/test/resources/json/vacancy/vacancies_correctlyDeserializesVacancies_includingSomeWithoutRecruiterCompany.json");

        List<VacancyDto> vacancyDtos = dtoSerializer.jsonFileToVacancyDtoList(jsonFile);

        assertThat(vacancyDtos).hasSize(4);

        assertThat(vacancyDtos.get(0).getPosition()).isEqualTo("Full-Stack developer");
        assertThat(vacancyDtos.get(0).getSalary()).isEqualTo(1000F);
        assertThat(vacancyDtos.get(0).getTechnologyStack()).isEqualTo((List.of("Java", "Spring", "React")));
        assertThat(vacancyDtos.get(0).getRecruiterDto().getFirstName()).isEqualTo("Vladyslav");
        assertThat(vacancyDtos.get(0).getRecruiterDto().getLastName()).isEqualTo("Bondar");
        assertThat(vacancyDtos.get(0).getRecruiterDto().getCompanyName()).isNull();

        assertThat(vacancyDtos.get(1).getPosition()).isEqualTo("Data Scientist");
        assertThat(vacancyDtos.get(1).getSalary()).isEqualTo(1200F);
        assertThat(vacancyDtos.get(1).getTechnologyStack()).isEqualTo((List.of("Python", "Machine Learning")));
        assertThat(vacancyDtos.get(1).getRecruiterDto().getFirstName()).isEqualTo("Олена");
        assertThat(vacancyDtos.get(1).getRecruiterDto().getLastName()).isEqualTo("Петрова");
        assertThat(vacancyDtos.get(1).getRecruiterDto().getCompanyName()).isEqualTo("DataTech");

        assertThat(vacancyDtos.get(2).getPosition()).isEqualTo("Junior QA Engineer");
        assertThat(vacancyDtos.get(2).getSalary()).isEqualTo(800F);
        assertThat(vacancyDtos.get(2).getTechnologyStack()).isEqualTo((List.of("Selenium", "Cypress")));
        assertThat(vacancyDtos.get(2).getRecruiterDto().getFirstName()).isEqualTo("Іван");
        assertThat(vacancyDtos.get(2).getRecruiterDto().getLastName()).isEqualTo("Франко");
        assertThat(vacancyDtos.get(2).getRecruiterDto().getCompanyName()).isNull();

        assertThat(vacancyDtos.get(3).getPosition()).isEqualTo("Frontend Developer");
        assertThat(vacancyDtos.get(3).getSalary()).isEqualTo(900F);
        assertThat(vacancyDtos.get(3).getTechnologyStack()).isEqualTo((List.of("React", "Angular", "Vue.js")));
        assertThat(vacancyDtos.get(3).getRecruiterDto().getFirstName()).isEqualTo("Марія");
        assertThat(vacancyDtos.get(3).getRecruiterDto().getLastName()).isEqualTo("Степаненко");
        assertThat(vacancyDtos.get(3).getRecruiterDto().getCompanyName()).isNull();
    }

    @Test
    void jsonFileToVacancyDtoList_correctlyDeserializesVacancies_includingSomeWithoutSalary() throws Exception {
        File jsonFile = new File("src/test/resources/json/vacancy/vacancies_correctlyDeserializesVacancies_includingSomeWithoutSalary.json");

        List<VacancyDto> vacancyDtos = dtoSerializer.jsonFileToVacancyDtoList(jsonFile);

        assertThat(vacancyDtos).hasSize(4);

        assertThat(vacancyDtos.get(0).getPosition()).isEqualTo("Full-Stack developer");
        assertThat(vacancyDtos.get(0).getSalary()).isNull();
        assertThat(vacancyDtos.get(0).getTechnologyStack()).isEqualTo((List.of("Java", "Spring", "React")));
        assertThat(vacancyDtos.get(0).getRecruiterDto().getFirstName()).isEqualTo("Vladyslav");
        assertThat(vacancyDtos.get(0).getRecruiterDto().getLastName()).isEqualTo("Bondar");
        assertThat(vacancyDtos.get(0).getRecruiterDto().getCompanyName()).isEqualTo("ProfITsoft");

        assertThat(vacancyDtos.get(1).getPosition()).isEqualTo("Data Scientist");
        assertThat(vacancyDtos.get(1).getSalary()).isNull();
        assertThat(vacancyDtos.get(1).getTechnologyStack()).isEqualTo((List.of("Python", "Machine Learning")));
        assertThat(vacancyDtos.get(1).getRecruiterDto().getFirstName()).isEqualTo("Олена");
        assertThat(vacancyDtos.get(1).getRecruiterDto().getLastName()).isEqualTo("Петрова");
        assertThat(vacancyDtos.get(1).getRecruiterDto().getCompanyName()).isEqualTo("DataTech");

        assertThat(vacancyDtos.get(2).getPosition()).isEqualTo("Junior QA Engineer");
        assertThat(vacancyDtos.get(2).getSalary()).isEqualTo(800F);
        assertThat(vacancyDtos.get(2).getTechnologyStack()).isEqualTo((List.of("Selenium", "Cypress")));
        assertThat(vacancyDtos.get(2).getRecruiterDto().getFirstName()).isEqualTo("Іван");
        assertThat(vacancyDtos.get(2).getRecruiterDto().getLastName()).isEqualTo("Франко");
        assertThat(vacancyDtos.get(2).getRecruiterDto().getCompanyName()).isEqualTo("SoftServe");

        assertThat(vacancyDtos.get(3).getPosition()).isEqualTo("Frontend Developer");
        assertThat(vacancyDtos.get(3).getSalary()).isNull();
        assertThat(vacancyDtos.get(3).getTechnologyStack()).isEqualTo((List.of("React", "Angular", "Vue.js")));
        assertThat(vacancyDtos.get(3).getRecruiterDto().getFirstName()).isEqualTo("Марія");
        assertThat(vacancyDtos.get(3).getRecruiterDto().getLastName()).isEqualTo("Степаненко");
        assertThat(vacancyDtos.get(3).getRecruiterDto().getCompanyName()).isEqualTo("EPAM");
    }

    @Test
    void jsonFileToVacancyDtoList_correctlyDeserializesVacancies_includingSomeWithoutTechStack() throws Exception {
        File jsonFile = new File("src/test/resources/json/vacancy/vacancies_correctlyDeserializesVacancies_includingSomeWithoutTechStack.json");

        List<VacancyDto> vacancyDtos = dtoSerializer.jsonFileToVacancyDtoList(jsonFile);

        assertThat(vacancyDtos).hasSize(4);

        assertThat(vacancyDtos.get(0).getPosition()).isEqualTo("Full-Stack developer");
        assertThat(vacancyDtos.get(0).getSalary()).isEqualTo(1000F);
        assertThat(vacancyDtos.get(0).getTechnologyStack()).isNull();
        assertThat(vacancyDtos.get(0).getRecruiterDto().getFirstName()).isEqualTo("Vladyslav");
        assertThat(vacancyDtos.get(0).getRecruiterDto().getLastName()).isEqualTo("Bondar");
        assertThat(vacancyDtos.get(0).getRecruiterDto().getCompanyName()).isEqualTo("ProfITsoft");

        assertThat(vacancyDtos.get(1).getPosition()).isEqualTo("Data Scientist");
        assertThat(vacancyDtos.get(1).getSalary()).isEqualTo(1200F);
        assertThat(vacancyDtos.get(1).getTechnologyStack()).isEqualTo((List.of("Python", "Machine Learning")));
        assertThat(vacancyDtos.get(1).getRecruiterDto().getFirstName()).isEqualTo("Олена");
        assertThat(vacancyDtos.get(1).getRecruiterDto().getLastName()).isEqualTo("Петрова");
        assertThat(vacancyDtos.get(1).getRecruiterDto().getCompanyName()).isEqualTo("DataTech");

        assertThat(vacancyDtos.get(2).getPosition()).isEqualTo("Junior QA Engineer");
        assertThat(vacancyDtos.get(2).getSalary()).isEqualTo(800F);
        assertThat(vacancyDtos.get(2).getTechnologyStack()).isNull();
        assertThat(vacancyDtos.get(2).getRecruiterDto().getFirstName()).isEqualTo("Іван");
        assertThat(vacancyDtos.get(2).getRecruiterDto().getLastName()).isEqualTo("Франко");
        assertThat(vacancyDtos.get(2).getRecruiterDto().getCompanyName()).isEqualTo("SoftServe");

        assertThat(vacancyDtos.get(3).getPosition()).isEqualTo("Frontend Developer");
        assertThat(vacancyDtos.get(3).getSalary()).isEqualTo(900F);
        assertThat(vacancyDtos.get(3).getTechnologyStack()).isNull();
        assertThat(vacancyDtos.get(3).getRecruiterDto().getFirstName()).isEqualTo("Марія");
        assertThat(vacancyDtos.get(3).getRecruiterDto().getLastName()).isEqualTo("Степаненко");
        assertThat(vacancyDtos.get(3).getRecruiterDto().getCompanyName()).isEqualTo("EPAM");
    }

    @Test
    void jsonFileToVacancyDtoList_throwsJsonMappingException_whenVacancyDtoListContainsVacancyPositionIsNull() {
        File jsonFile = new File("src/test/resources/json/vacancy/vacancies_throwsJsonMappingException_whenVacancyDtoListContainsVacancyPositionIsNull.json");

        assertThrows(JsonMappingException.class, () -> dtoSerializer.jsonFileToVacancyDtoList(jsonFile));
    }

    @Test
    void jsonFileToVacancyDtoList_throwsJsonMappingException_whenVacancyDtoListContainsVacancyWithoutPosition() {
        File jsonFile = new File("src/test/resources/json/vacancy/vacancies_throwsJsonMappingException_whenVacancyDtoListContainsVacancyWithoutPosition.json");

        assertThrows(JsonMappingException.class, () -> dtoSerializer.jsonFileToVacancyDtoList(jsonFile));
    }

    @Test
    void objectToXmlFile_correctlySerializesPositionVacancyStatsDto() throws Exception {
        PositionVacancyStatsDto dto = new PositionVacancyStatsDto();
        LinkedHashMap<String, Long> vacancyCountByPosition = new LinkedHashMap<>();
        vacancyCountByPosition.put("Java Developer", 10L);
        vacancyCountByPosition.put("Python Developer", 5L);
        dto.setVacancyCountByPosition(vacancyCountByPosition);

        File tempFile = File.createTempFile("temp-file", ".xml");
        tempFile.deleteOnExit();

        dtoSerializer.objectToXmlFile(dto, tempFile);

        assertTrue(Files.exists(Paths.get(tempFile.getPath())));
        String contentFile = Files.lines(Paths.get(tempFile.getPath())).collect(Collectors.joining("\n"));

        assertThat(contentFile).isEqualTo("""
                <?xml version='1.0' encoding='UTF-8'?>
                <statistic>
                  <vacancy-count-by-position-statistic>
                    <item>
                      <key>Java Developer</key>
                      <count>10</count>
                    </item>
                    <item>
                      <key>Python Developer</key>
                      <count>5</count>
                    </item>
                  </vacancy-count-by-position-statistic>
                </statistic>""");
    }

    @Test
    void objectToXmlFile_correctlySerializesRecruiterVacancyStatsDto() throws Exception {
        RecruiterVacancyStatsDto dto = new RecruiterVacancyStatsDto();

        LinkedHashMap<RecruiterDto, Long> vacancyCountByPosition = new LinkedHashMap<>();
        vacancyCountByPosition.put(new RecruiterDto("Vladyslav", "Bondar", "ProfITsoft"), 10L);
        vacancyCountByPosition.put(new RecruiterDto("Олена", "Петрова", "DataTech"), 5L);


        dto.setVacancyCountByRecruiter(vacancyCountByPosition);

        File tempFile = File.createTempFile("temp-file", ".xml");
        tempFile.deleteOnExit();

        dtoSerializer.objectToXmlFile(dto, tempFile);

        assertTrue(Files.exists(Paths.get(tempFile.getPath())));
        String contentFile = Files.lines(Paths.get(tempFile.getPath())).collect(Collectors.joining("\n"));

        assertThat(contentFile).isEqualTo("""
                <?xml version='1.0' encoding='UTF-8'?>
                <statistic>
                  <vacancy-count-by-recruiter-statistic>
                    <item>
                      <key>RecruiterDto(firstName=Vladyslav, lastName=Bondar, companyName=ProfITsoft)</key>
                      <count>10</count>
                    </item>
                    <item>
                      <key>RecruiterDto(firstName=Олена, lastName=Петрова, companyName=DataTech)</key>
                      <count>5</count>
                    </item>
                  </vacancy-count-by-recruiter-statistic>
                </statistic>""");
    }

    @Test
    void objectToXmlFile_correctlySerializesSalaryVacancyStatsDto() throws Exception {
        SalaryVacancyStatsDto dto = new SalaryVacancyStatsDto();
        dto.setMaxSalary(1200D);
        dto.setMinSalary(1000D);
        dto.setAverageSalary((double) ((1000 * 10 + 1200 * 5 + 1010 * 3) / (10 + 5 + 3)));

        LinkedHashMap<Float, Long> vacancyCountBySalary = new LinkedHashMap<>();
        vacancyCountBySalary.put(1000F, 10L);
        vacancyCountBySalary.put(1200F, 5L);
        vacancyCountBySalary.put(1010F, 3L);

        dto.setVacancyCountBySalary(vacancyCountBySalary);

        File tempFile = File.createTempFile("temp-file", ".xml");
        tempFile.deleteOnExit();

        dtoSerializer.objectToXmlFile(dto, tempFile);

        assertTrue(Files.exists(Paths.get(tempFile.getPath())));
        String contentFile = Files.lines(Paths.get(tempFile.getPath())).collect(Collectors.joining("\n"));

        assertThat(contentFile).isEqualTo("""
                <?xml version='1.0' encoding='UTF-8'?>
                <statistic>
                  <min-salary>1000.0</min-salary>
                  <average-salary>1057.0</average-salary>
                  <max-salary>1200.0</max-salary>
                  <vacancy-count-by-salary-statistic>
                    <item>
                      <key>1000.0</key>
                      <count>10</count>
                    </item>
                    <item>
                      <key>1200.0</key>
                      <count>5</count>
                    </item>
                    <item>
                      <key>1010.0</key>
                      <count>3</count>
                    </item>
                  </vacancy-count-by-salary-statistic>
                </statistic>""");
    }

    @Test
    void objectToXmlFile_correctlySerializesTechnologyStackVacancyStatsDto() throws Exception {
        TechnologyVacancyStatsDto dto = new TechnologyVacancyStatsDto();

        LinkedHashMap<String, Long> vacancyCountByTechnologyStack = new LinkedHashMap<>();
        vacancyCountByTechnologyStack.put("Java", 10L);
        vacancyCountByTechnologyStack.put("Python", 5L);
        vacancyCountByTechnologyStack.put("Cypress", 3L);

        dto.setVacancyCountByTechnology(vacancyCountByTechnologyStack);

        File tempFile = File.createTempFile("temp-file", ".xml");
        tempFile.deleteOnExit();

        dtoSerializer.objectToXmlFile(dto, tempFile);

        assertTrue(Files.exists(Paths.get(tempFile.getPath())));
        String contentFile = Files.lines(Paths.get(tempFile.getPath())).collect(Collectors.joining("\n"));

        assertThat(contentFile).isEqualTo("""
                <?xml version='1.0' encoding='UTF-8'?>
                <statistic>
                  <vacancy-count-by-technology-statistic>
                    <item>
                      <key>Java</key>
                      <count>10</count>
                    </item>
                    <item>
                      <key>Python</key>
                      <count>5</count>
                    </item>
                    <item>
                      <key>Cypress</key>
                      <count>3</count>
                    </item>
                  </vacancy-count-by-technology-statistic>
                </statistic>""");
    }
}