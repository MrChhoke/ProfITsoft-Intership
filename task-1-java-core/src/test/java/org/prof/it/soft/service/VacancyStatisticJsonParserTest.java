package org.prof.it.soft.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import org.junit.jupiter.api.Test;
import org.prof.it.soft.dto.RecruiterDto;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class VacancyStatisticJsonParserTest {

    @Test
    public void processJsonFile_returnsEmptyMap_forEmptyInput() throws IOException {
        JsonParser jsonParser = new JsonFactory().createParser("[]");
        VacancyStatisticJsonParser vacancyStatisticJsonParser = new VacancyStatisticJsonParser(jsonParser);

        Map<Object, Long> result = vacancyStatisticJsonParser.processJsonFile("position");
        assertThat(result).isEmpty();
    }

    @Test
    public void processJsonFile_correctlyCountsPositions_fromValidEntries() throws IOException {
        JsonParser jsonParser = new JsonFactory().createParser("""
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
                    "position": "Full-Stack developer",
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
                """);
        VacancyStatisticJsonParser vacancyStatisticJsonParser = new VacancyStatisticJsonParser(jsonParser);
        Map<Object, Long> result = vacancyStatisticJsonParser.processJsonFile("position");

        assertThat(result.get("Full-Stack developer")).isEqualTo(2);
        assertThat(result.get("Junior QA Engineer")).isEqualTo(1);
        assertThat(result.get("Frontend Developer")).isEqualTo(1);
        assertThat(result.get("Backend Developer")).isNull();
    }

    @Test
    public void processJsonFile_correctlyHandlesEntries_withNullPosition() throws IOException {
        JsonParser jsonParser = new JsonFactory().createParser("""
                [
                  {
                    "position": null,
                    "salary": 1000.0,
                    "technology_stack": "Java, Spring, React",
                    "recruiter_first_name": "Vladyslav",
                    "recruiter_last_name": "Bondar",
                    "recruiter_company_name": "ProfITsoft"
                  },
                  {
                    "position": "Full-Stack developer",
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
                """);
        VacancyStatisticJsonParser vacancyStatisticJsonParser = new VacancyStatisticJsonParser(jsonParser);
        Map<Object, Long> result = vacancyStatisticJsonParser.processJsonFile("position");

        assertThat(result.get("Full-Stack developer")).isEqualTo(1);
        assertThat(result.get("Junior QA Engineer")).isEqualTo(1);
        assertThat(result.get("Frontend Developer")).isEqualTo(1);
        assertThat(result.get("Backend Developer")).isNull();
    }

    @Test
    public void processJsonFile_correctlyCountsSalaries_fromValidEntries() throws IOException {
        JsonParser jsonParser = new JsonFactory().createParser("""
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
                    "position": "Full-Stack developer",
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
                """);
        VacancyStatisticJsonParser vacancyStatisticJsonParser = new VacancyStatisticJsonParser(jsonParser);
        Map<Object, Long> result = vacancyStatisticJsonParser.processJsonFile("position");

        assertThat(result.get("Full-Stack developer")).isEqualTo(2);
        assertThat(result.get("Junior QA Engineer")).isNull();
        assertThat(result.get("Frontend Developer")).isEqualTo(1);
        assertThat(result.get("Backend Developer")).isNull();
    }

    @Test
    public void processJsonFile_correctlyCountsSalaries_fromFullData() throws IOException {
        JsonParser jsonParser = new JsonFactory().createParser("""
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
                    "position": "Full-Stack developer",
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
                    "salary": 1200.0,
                    "technology_stack": "React, Angular, Vue.js",
                    "recruiter_first_name": "Марія",
                    "recruiter_last_name": "Степаненко",
                    "recruiter_company_name": "EPAM"
                  }
                ]
                """);
        VacancyStatisticJsonParser vacancyStatisticJsonParser = new VacancyStatisticJsonParser(jsonParser);
        Map<Object, Long> result = vacancyStatisticJsonParser.processJsonFile("salary");

        assertThat(result.get("1000.0")).isEqualTo(1);
        assertThat(result.get("1200.0")).isEqualTo(2);
        assertThat(result.get("800.0")).isEqualTo(1);
    }

    @Test
    public void processJsonFile_ignoresSalaries_withNegativeValues() throws IOException {
        JsonParser jsonParser = new JsonFactory().createParser("""
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
                    "position": "Full-Stack developer",
                    "salary": -1200.0,
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
                    "salary": 1200.0,
                    "technology_stack": "React, Angular, Vue.js",
                    "recruiter_first_name": "Марія",
                    "recruiter_last_name": "Степаненко",
                    "recruiter_company_name": "EPAM"
                  }
                ]
                """);
        VacancyStatisticJsonParser vacancyStatisticJsonParser = new VacancyStatisticJsonParser(jsonParser);
        Map<Object, Long> result = vacancyStatisticJsonParser.processJsonFile("salary");

        assertThat(result.get("1000.0")).isEqualTo(1);
        assertThat(result.get("1200.0")).isEqualTo(1);
        assertThat(result.get("800.0")).isEqualTo(1);
        assertThat(result.get("1800.0")).isNull();
    }

    @Test
    public void processJsonFile_correctlyHandlesSalaries_withAbsentSalaryFields() throws IOException {
        JsonParser jsonParser = new JsonFactory().createParser("""
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
                    "position": "Full-Stack developer",
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
                    "salary": 1200.0,
                    "technology_stack": "React, Angular, Vue.js",
                    "recruiter_first_name": "Марія",
                    "recruiter_last_name": "Степаненко",
                    "recruiter_company_name": "EPAM"
                  }
                ]
                """);
        VacancyStatisticJsonParser vacancyStatisticJsonParser = new VacancyStatisticJsonParser(jsonParser);
        Map<Object, Long> result = vacancyStatisticJsonParser.processJsonFile("salary");

        assertThat(result.get("1000.0")).isEqualTo(1);
        assertThat(result.get("1200.0")).isEqualTo(1);
        assertThat(result.get("800.0")).isEqualTo(1);
        assertThat(result.get("1800.0")).isNull();
    }

    @Test
    public void processJsonFile_correctlyHandlesSalaries_withNullValues() throws IOException {
        JsonParser jsonParser = new JsonFactory().createParser("""
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
                    "position": "Full-Stack developer",
                    "salary": 1200,
                    "technology_stack": "Python, Machine Learning",
                    "recruiter_first_name": "Олена",
                    "recruiter_last_name": "Петрова",
                    "recruiter_company_name": "DataTech"
                  },
                  {
                    "position": "Junior QA Engineer",
                    "salary": null,
                    "technology_stack": "Selenium, Cypress",
                    "recruiter_first_name": "Іван",
                    "recruiter_last_name": "Франко",
                    "recruiter_company_name": "SoftServe"
                  },
                  {
                    "position": "Frontend Developer",
                    "salary": 1200.0,
                    "technology_stack": "React, Angular, Vue.js",
                    "recruiter_first_name": "Марія",
                    "recruiter_last_name": "Степаненко",
                    "recruiter_company_name": "EPAM"
                  }
                ]
                """);
        VacancyStatisticJsonParser vacancyStatisticJsonParser = new VacancyStatisticJsonParser(jsonParser);
        Map<Object, Long> result = vacancyStatisticJsonParser.processJsonFile("salary");
        assertThat(result.get("1000.0")).isEqualTo(1);
        assertThat(result.get("1200.0")).isEqualTo(2);
        assertThat(result.get("800.0")).isNull();
        assertThat(result.get("1800.0")).isNull();
    }

    @Test
    public void processJsonFile_correctlyHandlesSalaries_withInvalidEntries() throws IOException {
        JsonParser jsonParser = new JsonFactory().createParser("""
                [
                  {
                    "position": "Full-Stack developer",
                    "salary": 1000.0,
                    "technology_stack": "Java, Spring, React",
                    "recruiter_last_name": "Bondar",
                    "recruiter_company_name": "ProfITsoft"
                  },
                  {
                    "position": "Full-Stack developer",
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
                    "salary": 1200.0,
                    "technology_stack": "React, Angular, Vue.js",
                    "recruiter_first_name": "Марія",
                    "recruiter_last_name": "Степаненко",
                    "recruiter_company_name": "EPAM"
                  }
                ]
                """);
        VacancyStatisticJsonParser vacancyStatisticJsonParser = new VacancyStatisticJsonParser(jsonParser);
        Map<Object, Long> result = vacancyStatisticJsonParser.processJsonFile("salary");

        assertThat(result.get("1000.0")).isNull();
        assertThat(result.get("1200.0")).isEqualTo(2);
        assertThat(result.get("800.0")).isNull();
        assertThat(result.get("1800.0")).isNull();
    }

    @Test
    public void processJsonFile_correctlyCountsTechnologies_fromMultipleStacks() throws IOException {
        JsonParser jsonParser = new JsonFactory().createParser("""
                [
                  {
                    "position": "Full-Stack developer",
                    "salary": 1000.0,
                    "technology_stack": "Java, Spring, React, Spring-Boot, Spring Data",
                    "recruiter_first_name": "Vladyslav",
                    "recruiter_last_name": "Bondar",
                    "recruiter_company_name": "ProfITsoft"
                  },
                  {
                    "position": "Full-Stack developer",
                    "salary": 1200.0,
                    "technology_stack": "Python, Machine Learning",
                    "recruiter_first_name": "Олена",
                    "recruiter_last_name": "Петрова",
                    "recruiter_company_name": "DataTech"
                  },
                  {
                    "position": "Junior QA Engineer",
                    "salary": 800.0,
                    "technology_stack": "Selenium, Cypress, Python",
                    "recruiter_first_name": "Іван",
                    "recruiter_last_name": "Франко",
                    "recruiter_company_name": "SoftServe"
                  },
                  {
                    "position": "Frontend Developer",
                    "salary": 1200.0,
                    "technology_stack": "React, Angular, Vue.js, Redux-React",
                    "recruiter_first_name": "Марія",
                    "recruiter_last_name": "Степаненко",
                    "recruiter_company_name": "EPAM"
                  }
                ]
                """);
        VacancyStatisticJsonParser vacancyStatisticJsonParser = new VacancyStatisticJsonParser(jsonParser);
        Map<Object, Long> result = vacancyStatisticJsonParser.processJsonFile("technology_stack");
        assertThat(result.get("React")).isEqualTo(2);
        assertThat(result.get("Python")).isEqualTo(2);
        assertThat(result.get("Java")).isEqualTo(1);
        assertThat(result.get("Spring")).isEqualTo(1);
        assertThat(result.get("Machine Learning")).isEqualTo(1);
        assertThat(result.get("Selenium")).isEqualTo(1);
        assertThat(result.get("Cypress")).isEqualTo(1);
        assertThat(result.get("Angular")).isEqualTo(1);
        assertThat(result.get("Vue.js")).isEqualTo(1);
        assertThat(result.get("Spring-Boot")).isEqualTo(1);
        assertThat(result.get("Redux-React")).isEqualTo(1);
        assertThat(result.get("Spring Data")).isEqualTo(1);
        assertThat(result.get("Googling")).isNull();
        assertThat(result.get("C++")).isNull();
        assertThat(result.get("C")).isNull();
        assertThat(result.get("Google API")).isNull();
        assertThat(result.get("Git")).isNull();
    }

    @Test
    public void processJsonFile_correctlyCountsTechnologies_excludingNullStacks() throws IOException {
        JsonParser jsonParser = new JsonFactory().createParser("""
                [
                  {
                    "position": "Full-Stack developer",
                    "salary": 1000.0,
                    "technology_stack": null,
                    "recruiter_first_name": "Vladyslav",
                    "recruiter_last_name": "Bondar",
                    "recruiter_company_name": "ProfITsoft"
                  },
                  {
                    "position": "Full-Stack developer",
                    "salary": 1200.0,
                    "technology_stack": null,
                    "recruiter_first_name": "Олена",
                    "recruiter_last_name": "Петрова",
                    "recruiter_company_name": "DataTech"
                  },
                  {
                    "position": "Junior QA Engineer",
                    "salary": 800.0,
                    "technology_stack": "Selenium, Cypress, Python",
                    "recruiter_first_name": "Іван",
                    "recruiter_last_name": "Франко",
                    "recruiter_company_name": "SoftServe"
                  },
                  {
                    "position": "Frontend Developer",
                    "salary": 1200.0,
                    "technology_stack": "React, Angular, Vue.js, Redux-React",
                    "recruiter_first_name": "Марія",
                    "recruiter_last_name": "Степаненко",
                    "recruiter_company_name": "EPAM"
                  }
                ]
                """);
        VacancyStatisticJsonParser vacancyStatisticJsonParser = new VacancyStatisticJsonParser(jsonParser);
        Map<Object, Long> result = vacancyStatisticJsonParser.processJsonFile("technology_stack");
        assertThat(result.get("React")).isEqualTo(1);
        assertThat(result.get("Python")).isEqualTo(1);
        assertThat(result.get("Selenium")).isEqualTo(1);
        assertThat(result.get("Cypress")).isEqualTo(1);
        assertThat(result.get("Angular")).isEqualTo(1);
        assertThat(result.get("Vue.js")).isEqualTo(1);
        assertThat(result.get("Redux-React")).isEqualTo(1);
        assertThat(result.get("Googling")).isNull();
        assertThat(result.get("C++")).isNull();
        assertThat(result.get("C")).isNull();
        assertThat(result.get("Google API")).isNull();
        assertThat(result.get("Git")).isNull();
    }

    @Test
    public void processJsonFile_correctlyCountsTechnologies_withAbsentTechnologyStacks() throws IOException {
        JsonParser jsonParser = new JsonFactory().createParser("""
                [
                  {
                    "position": "Full-Stack developer",
                    "salary": 1000.0,
                    "recruiter_first_name": "Vladyslav",
                    "recruiter_last_name": "Bondar",
                    "recruiter_company_name": "ProfITsoft"
                  },
                  {
                    "position": "Full-Stack developer",
                    "salary": 1200.0,
                    "recruiter_first_name": "Олена",
                    "recruiter_last_name": "Петрова",
                    "recruiter_company_name": "DataTech"
                  },
                  {
                    "position": "Junior QA Engineer",
                    "salary": 800.0,
                    "technology_stack": "Selenium, Cypress, Python",
                    "recruiter_first_name": "Іван",
                    "recruiter_last_name": "Франко",
                    "recruiter_company_name": "SoftServe"
                  },
                  {
                    "position": "Frontend Developer",
                    "salary": 1200.0,
                    "technology_stack": "React, Angular, Vue.js, Redux-React",
                    "recruiter_first_name": "Марія",
                    "recruiter_last_name": "Степаненко",
                    "recruiter_company_name": "EPAM"
                  }
                ]
                """);
        VacancyStatisticJsonParser vacancyStatisticJsonParser = new VacancyStatisticJsonParser(jsonParser);
        Map<Object, Long> result = vacancyStatisticJsonParser.processJsonFile("technology_stack");
        assertThat(result.get("React")).isEqualTo(1);
        assertThat(result.get("Python")).isEqualTo(1);
        assertThat(result.get("Selenium")).isEqualTo(1);
        assertThat(result.get("Cypress")).isEqualTo(1);
        assertThat(result.get("Angular")).isEqualTo(1);
        assertThat(result.get("Vue.js")).isEqualTo(1);
        assertThat(result.get("Redux-React")).isEqualTo(1);
        assertThat(result.get("Googling")).isNull();
        assertThat(result.get("C++")).isNull();
        assertThat(result.get("C")).isNull();
        assertThat(result.get("Google API")).isNull();
        assertThat(result.get("Git")).isNull();
    }

    @Test
    public void processJsonFile_correctlyCountsRecruiters_withSomeNullFields() throws IOException {
        JsonParser jsonParser = new JsonFactory().createParser("""
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
                    "position": "Full-Stack developer",
                    "salary": 1200.0,
                    "technology_stack": "Python, Machine Learning",
                    "recruiter_first_name": "Vladyslav",
                    "recruiter_company_name": "ProfITsoft"
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
                    "recruiter_first_name": "Vladyslav",
                    "recruiter_last_name": "Bondar",
                    "recruiter_company_name": "ProfITsoft"
                  },
                  {
                    "position": "Full-Stack developer",
                    "salary": 2000.0,
                    "technology_stack": "Java, Spring Boot, React",
                    "recruiter_first_name": "Іван",
                    "recruiter_last_name": "Франко",
                    "recruiter_company_name": "SoftServe"
                  },
                  {
                    "position": "Full-Stack developer",
                    "salary": 1500.0,
                    "technology_stack": "PhP, SQL, HTML",
                    "recruiter_first_name": "Vladyslav",
                    "recruiter_last_name": "Bondar",
                    "recruiter_company_name": "SoftServe"
                  }
                ]
                """);
        VacancyStatisticJsonParser vacancyStatisticJsonParser = new VacancyStatisticJsonParser(jsonParser);
        Map<Object, Long> result = vacancyStatisticJsonParser.processJsonFile("recruiter");

        assertThat(result).hasSize(4);
        assertThat(result.get(new RecruiterDto("Vladyslav", "Bondar", "ProfITsoft"))).isEqualTo(2);
        assertThat(result.get(new RecruiterDto("Іван", "Франко", "SoftServe"))).isEqualTo(2);
        assertThat(result.get(new RecruiterDto("Vladyslav", "Bondar", "SoftServe"))).isEqualTo(1);
        assertThat(result.get(new RecruiterDto("Vladyslav", null, "ProfITsoft"))).isEqualTo(1);
    }

    @Test
    public void processJsonFile_correctlyCountsRecruiters__excludingNullRecruiterFields() throws IOException {
        JsonParser jsonParser = new JsonFactory().createParser("""
                [
                  {
                    "position": "Full-Stack developer",
                    "salary": 1000.0,
                    "technology_stack": "Java, Spring, React",
                    "recruiter_first_name": null,
                    "recruiter_last_name": "Bondar",
                    "recruiter_company_name": null
                  },
                  {
                    "position": "Full-Stack developer",
                    "salary": 1200.0,
                    "technology_stack": "Python, Machine Learning",
                    "recruiter_first_name": "Vladyslav",
                    "recruiter_company_name": null
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
                    "recruiter_first_name": "Vladyslav",
                    "recruiter_last_name": "Bondar",
                    "recruiter_company_name": "ProfITsoft"
                  },
                  {
                    "position": "Full-Stack developer",
                    "salary": 2000.0,
                    "technology_stack": "Java, Spring Boot, React",
                    "recruiter_first_name": "Іван",
                    "recruiter_last_name": "Франко",
                    "recruiter_company_name": "SoftServe"
                  },
                  {
                    "position": "Full-Stack developer",
                    "salary": 1500.0,
                    "technology_stack": "PhP, SQL, HTML",
                    "recruiter_first_name": "Vladyslav",
                    "recruiter_last_name": "Bondar",
                    "recruiter_company_name": "SoftServe"
                  }
                ]
                """);
        VacancyStatisticJsonParser vacancyStatisticJsonParser = new VacancyStatisticJsonParser(jsonParser);
        Map<Object, Long> result = vacancyStatisticJsonParser.processJsonFile("recruiter");

        assertThat(result).hasSize(4);
        assertThat(result.get(new RecruiterDto("Vladyslav", "Bondar", "ProfITsoft"))).isEqualTo(1);
        assertThat(result.get(new RecruiterDto("Іван", "Франко", "SoftServe"))).isEqualTo(2);
        assertThat(result.get(new RecruiterDto("Vladyslav", null, null))).isEqualTo(1);
        assertThat(result.get(new RecruiterDto(null, "Bondar", null))).isNull();
        assertThat(result.get(new RecruiterDto("Іван", "Франко", null))).isNull();
    }

    @Test
    public void processJsonFile_correctlyCountsRecruiters_withAbsentRecruiterFields () throws IOException {
        JsonParser jsonParser = new JsonFactory().createParser("""
                [
                  {
                    "position": "Full-Stack developer",
                    "salary": 1000.0,
                    "technology_stack": "Java, Spring, React",
                    "recruiter_last_name": "Bondar"
                  },
                  {
                    "position": "Full-Stack developer",
                    "salary": 1200.0,
                    "technology_stack": "Python, Machine Learning",
                    "recruiter_first_name": "Vladyslav"
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
                    "recruiter_first_name": "Vladyslav",
                    "recruiter_last_name": "Bondar",
                    "recruiter_company_name": "ProfITsoft"
                  },
                  {
                    "position": "Full-Stack developer",
                    "salary": 2000.0,
                    "technology_stack": "Java, Spring Boot, React",
                    "recruiter_first_name": "Іван",
                    "recruiter_last_name": "Франко",
                    "recruiter_company_name": "SoftServe"
                  },
                  {
                    "position": "Full-Stack developer",
                    "salary": 1500.0,
                    "technology_stack": "PhP, SQL, HTML",
                    "recruiter_first_name": "Vladyslav",
                    "recruiter_last_name": "Bondar",
                    "recruiter_company_name": "SoftServe"
                  }
                ]
                """);
        VacancyStatisticJsonParser vacancyStatisticJsonParser = new VacancyStatisticJsonParser(jsonParser);
        Map<Object, Long> result = vacancyStatisticJsonParser.processJsonFile("recruiter");

        assertThat(result).hasSize(4);
        assertThat(result.get(new RecruiterDto("Vladyslav", "Bondar", "ProfITsoft"))).isEqualTo(1);
        assertThat(result.get(new RecruiterDto("Іван", "Франко", "SoftServe"))).isEqualTo(2);
        assertThat(result.get(new RecruiterDto("Vladyslav", null, null))).isEqualTo(1);
        assertThat(result.get(new RecruiterDto(null, "Bondar", null))).isNull();
        assertThat(result.get(new RecruiterDto("Іван", "Франко", null))).isNull();
    }

    @Test
    public void processEndObject_shouldIncrementRecruiterValue_whenStatisticFieldIsRecruiter() {
        Map<Object, Long> statisticMap = new HashMap<>();
        String statisticField = "recruiter";
        String recruiterFirstName = "John";
        String recruiterLastName = "Doe";
        String recruiterCompanyName = "Company";
        Object currentValue = null;

        JsonParser mockJsonParser = mock(JsonParser.class);
        VacancyStatisticJsonParser vacancyStatisticJsonParser = new VacancyStatisticJsonParser(mockJsonParser);
        vacancyStatisticJsonParser.processEndObject(statisticMap, statisticField, recruiterFirstName, recruiterLastName, recruiterCompanyName, currentValue);

        RecruiterDto recruiterDto = RecruiterDto.builder()
                .firstName(recruiterFirstName)
                .lastName(recruiterLastName)
                .companyName(recruiterCompanyName)
                .build();
        assertThat(statisticMap).containsEntry(recruiterDto, 1L);
    }

    @Test
    public void processEndObject_shouldIncrementTechnologyStackValue_whenStatisticFieldIsTechnologyStack() {
        Map<Object, Long> statisticMap = new HashMap<>();
        String statisticField = "technology_stack";
        String recruiterFirstName = null;
        String recruiterLastName = null;
        String recruiterCompanyName = null;
        Object currentValue = "Java, Python, C++";

        JsonParser mockJsonParser = mock(JsonParser.class);
        VacancyStatisticJsonParser vacancyStatisticJsonParser = new VacancyStatisticJsonParser(mockJsonParser);
        vacancyStatisticJsonParser.processEndObject(statisticMap, statisticField, recruiterFirstName, recruiterLastName, recruiterCompanyName, currentValue);

        assertThat(statisticMap).containsEntry("Java", 1L);
        assertThat(statisticMap).containsEntry("Python", 1L);
        assertThat(statisticMap).containsEntry("C++", 1L);
    }

    @Test
    public void processEndObject_shouldIncrementOtherValue_whenStatisticFieldIsNeitherRecruiterNorTechnologyStack() {
        Map<Object, Long> statisticMap = new HashMap<>();
        String statisticField = "other";
        String recruiterFirstName = null;
        String recruiterLastName = null;
        String recruiterCompanyName = null;
        Object currentValue = "value";

        JsonParser mockJsonParser = mock(JsonParser.class);
        VacancyStatisticJsonParser vacancyStatisticJsonParser = new VacancyStatisticJsonParser(mockJsonParser);
        vacancyStatisticJsonParser.processEndObject(statisticMap, statisticField, recruiterFirstName, recruiterLastName, recruiterCompanyName, currentValue);

        assertThat(statisticMap).containsEntry("value", 1L);
    }
}