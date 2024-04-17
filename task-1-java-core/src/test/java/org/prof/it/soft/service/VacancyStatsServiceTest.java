package org.prof.it.soft.service;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.prof.it.soft.config.Configuration;
import org.prof.it.soft.dto.AbstractDto;
import org.prof.it.soft.dto.RecruiterDto;
import org.prof.it.soft.dto.stats.PositionVacancyStatsDto;
import org.prof.it.soft.dto.stats.RecruiterVacancyStatsDto;
import org.prof.it.soft.dto.stats.SalaryVacancyStatsDto;
import org.prof.it.soft.dto.stats.TechnologyVacancyStatsDto;
import org.prof.it.soft.entity.Recruiter;
import org.prof.it.soft.entity.Vacancy;

import java.io.File;
import java.util.*;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

class VacancyStatsServiceTest {

    private final VacancyStatsService vacancyStatsService = new VacancyStatsService();
    private List<Vacancy> vacancies;
    private List<Recruiter> recruiters;

    @BeforeEach
    public void setup() {
        recruiters = List.of(
                new Recruiter("Vladyslav", "Bondar", "ProfITsoft", new HashSet<>()),
                new Recruiter("John", "Doe", "TechCorp", new HashSet<>()),
                new Recruiter("Anna", "Bell", "EPAM", new HashSet<>())
        );

        vacancies = List.of(
                Vacancy.builder().recruiter(recruiters.get(0)).salary(1000F).position("Full-Stack Developer").technologyStack(List.of("Java", "Spring", "React")).build(),
                Vacancy.builder().recruiter(recruiters.get(1)).salary(1000F).position("Data Scientist").technologyStack(List.of("Python", "TensorFlow", "SQL")).build(),
                Vacancy.builder().recruiter(recruiters.get(0)).salary(4600F).position("Frontend Developer").technologyStack(List.of("JavaScript", "Vue.js", "HTML", "CSS", "SQL")).build(),
                Vacancy.builder().recruiter(recruiters.get(1)).salary(2500F).position("Software Engineer").technologyStack(List.of("C++", "Qt", "Boost", "Python")).build(),
                Vacancy.builder().recruiter(recruiters.get(0)).salary(2500F).position("DevOps Engineer").technologyStack(List.of("Docker", "Kubernetes", "Jenkins", "SQL")).build(),
                Vacancy.builder().recruiter(recruiters.get(1)).salary(1500F).position("Full-Stack Developer").technologyStack(List.of("Java", "Angular", "Spring")).build(),
                Vacancy.builder().recruiter(recruiters.get(0)).position("Software Engineer").technologyStack(List.of("Boost", "C#", "C++")).build(),
                Vacancy.builder().recruiter(recruiters.get(2)).position("Software Engineer").technologyStack(List.of("C++", "C", "OpenGL")).build()
        );

        recruiters.get(0).addVacancy(vacancies.get(0));
        recruiters.get(1).addVacancy(vacancies.get(1));
        recruiters.get(0).addVacancy(vacancies.get(2));
        recruiters.get(1).addVacancy(vacancies.get(3));
        recruiters.get(0).addVacancy(vacancies.get(4));
        recruiters.get(1).addVacancy(vacancies.get(5));
        recruiters.get(0).addVacancy(vacancies.get(6));
        recruiters.get(2).addVacancy(vacancies.get(7));
    }

    @Test
    void calculateSalaryVacancyStats_shouldReturnCorrectStats() {
        SalaryVacancyStatsDto stats = vacancyStatsService.calculateSalaryVacancyStats(vacancies);
        assertThat(stats.getMaxSalary()).isEqualTo(4600D);
        assertThat(stats.getMinSalary()).isEqualTo(1000D);
        assertThat(stats.getAverageSalary()).isEqualTo(13100D / 6L);
        assertThat(stats.getVacancyCountBySalary()).isEqualTo((
                Map.of(1000F, 2L,
                        2500F, 2L,
                        4600F, 1L,
                        1500F, 1L)));

        assertThat(new ArrayList<>(stats.getVacancyCountBySalary().values())).isSortedAccordingTo(Comparator.reverseOrder());
    }

    @Test
    void calculatePositionVacancyStats_shouldReturnCorrectStats() {
        PositionVacancyStatsDto stats = vacancyStatsService.calculatePositionVacancyStats(vacancies);

        assertThat(stats.getVacancyCountByPosition()).isEqualTo(
                Map.of("Software Engineer", 3L,
                        "Full-Stack Developer", 2L,
                        "Frontend Developer", 1L,
                        "DevOps Engineer", 1L,
                        "Data Scientist", 1L));

        assertThat(new ArrayList<>(stats.getVacancyCountByPosition().values())).isSortedAccordingTo(Comparator.reverseOrder());
    }

    @Test
    void calculateRecruiterVacancyCountStats_shouldReturnCorrectStats() {
        RecruiterVacancyStatsDto stats = vacancyStatsService.calculateRecruiterVacancyCountStats(vacancies);

        List<RecruiterDto> recruiters = this.recruiters.stream().map(recr -> Configuration.getModelMapper().map(recr, RecruiterDto.class))
                .toList();

        assertThat(stats.getVacancyCountByRecruiter()).isEqualTo(
                Map.of(recruiters.get(0), 4L,
                        recruiters.get(1), 3L,
                        recruiters.get(2), 1L));

        assertThat(new ArrayList<>(stats.getVacancyCountByRecruiter().values())).isSortedAccordingTo(Comparator.reverseOrder());
    }

    @Test
    void calculateTechnologyVacancyStats_shouldReturnCorrectStats() {
        TechnologyVacancyStatsDto stats = vacancyStatsService.calculateTechnologyVacancyStats(vacancies);
        assertThat(stats.getVacancyCountByTechnology()).isEqualTo(
                Map.ofEntries(
                        Map.entry("Java", 2L),
                        Map.entry("Spring", 2L),
                        Map.entry("React", 1L),
                        Map.entry("Python", 2L),
                        Map.entry("TensorFlow", 1L),
                        Map.entry("SQL", 3L),
                        Map.entry("JavaScript", 1L),
                        Map.entry("Vue.js", 1L),
                        Map.entry("HTML", 1L),
                        Map.entry("CSS", 1L),
                        Map.entry("C++", 3L),
                        Map.entry("Qt", 1L),
                        Map.entry("Boost", 2L),
                        Map.entry("Docker", 1L),
                        Map.entry("Kubernetes", 1L),
                        Map.entry("Jenkins", 1L),
                        Map.entry("Angular", 1L),
                        Map.entry("C#", 1L),
                        Map.entry("C", 1L),
                        Map.entry("OpenGL", 1L)));

        assertThat(new ArrayList<>(stats.getVacancyCountByTechnology().values())).isSortedAccordingTo(Comparator.reverseOrder());
    }

    @Test
    void calculateVacancyStats_shouldReturnCorrectSalaryStats_whenStatisticFieldIsSalary() throws Exception {
        SalaryVacancyStatsDto salaryStatisticDto = (SalaryVacancyStatsDto) vacancyStatsService
                .calculateVacancyStats(new File("src/test/resources/json/vacancy/vacancies.json").getAbsoluteFile(), "salary");

        assertThat(salaryStatisticDto.getMaxSalary()).isEqualTo(4600D);
        assertThat(salaryStatisticDto.getMinSalary()).isEqualTo(1000D);
        assertThat(salaryStatisticDto.getAverageSalary()).isEqualTo(20200D / 8L);
        assertThat(salaryStatisticDto.getVacancyCountBySalary()).isEqualTo((
                Map.of(1000F, 2L,
                        2500F, 3L,
                        4600F, 2L,
                        1500F, 1L)));

        assertThat(new ArrayList<>(salaryStatisticDto.getVacancyCountBySalary().values())).isSortedAccordingTo(Comparator.reverseOrder());
    }

    @Test
    void calculateVacancyStats_shouldReturnCorrectPositionStats_whenStatisticFieldIsPosition() throws Exception {
        PositionVacancyStatsDto positionVacancyStatsDto = (PositionVacancyStatsDto) vacancyStatsService
                .calculateVacancyStats(new File("src/test/resources/json/vacancy/vacancies.json").getAbsoluteFile(), "position");

        assertThat(positionVacancyStatsDto.getVacancyCountByPosition()).isEqualTo(
                Map.of("Software Engineer", 3L,
                        "Full-Stack Developer", 3L,
                        "Frontend Developer", 2L,
                        "DevOps Engineer", 4L,
                        "Data Scientist", 2L));

        assertThat(new ArrayList<>(positionVacancyStatsDto.getVacancyCountByPosition().values())).isSortedAccordingTo(Comparator.reverseOrder());
    }

    @Test
    void calculateVacancyStats_shouldReturnCorrectRecruiterStats_whenStatisticFieldIsRecruiter() throws Exception {
        RecruiterVacancyStatsDto recruiterVacancyStatsDto = (RecruiterVacancyStatsDto) vacancyStatsService
                .calculateVacancyStats(new File("src/test/resources/json/vacancy/vacancies.json").getAbsoluteFile(), "recruiter");

        List<RecruiterDto> recruiterDtos = recruiterVacancyStatsDto.getVacancyCountByRecruiter().keySet().stream().toList();

        assertThat(recruiterDtos).hasSize(4);

        assertThat(recruiterDtos.get(0).getFirstName()).isEqualTo("Vladyslav");
        assertThat(recruiterDtos.get(0).getLastName()).isEqualTo("Bondar");
        assertThat(recruiterDtos.get(0).getCompanyName()).isEqualTo("ProfITsoft");

        assertThat(recruiterDtos.get(1).getFirstName()).isEqualTo("John");
        assertThat(recruiterDtos.get(1).getLastName()).isEqualTo("Doe");
        assertThat(recruiterDtos.get(1).getCompanyName()).isEqualTo("TechCorp");

        assertThat(recruiterDtos.get(2).getFirstName()).isEqualTo("Anna");
        assertThat(recruiterDtos.get(2).getLastName()).isEqualTo("Bell");
        assertThat(recruiterDtos.get(2).getCompanyName()).isEqualTo("EPAM");

        assertThat(recruiterDtos.get(3).getFirstName()).isEqualTo("Vladyslav");
        assertThat(recruiterDtos.get(3).getLastName()).isNull();
        assertThat(recruiterDtos.get(3).getCompanyName()).isEqualTo("SortServe");

        assertThat(recruiterVacancyStatsDto.getVacancyCountByRecruiter()).isEqualTo(
                Map.of(recruiterDtos.get(0), 8L,
                        recruiterDtos.get(1), 4L,
                        recruiterDtos.get(2), 1L,
                        recruiterDtos.get(3), 1L));

        assertThat(new ArrayList<>(recruiterVacancyStatsDto.getVacancyCountByRecruiter().values())).isSortedAccordingTo(Comparator.reverseOrder());
    }

    @Test
    void calculateVacancyStats_shouldReturnCorrectTechnologyStats_whenStatisticFieldIsTechnologyStack() throws Exception {
        TechnologyVacancyStatsDto recruiterVacancyCountStatsDto = (TechnologyVacancyStatsDto) vacancyStatsService
                .calculateVacancyStats(new File("src/test/resources/json/vacancy/vacancies.json").getAbsoluteFile(), "technology_stack");

        assertThat(recruiterVacancyCountStatsDto.getVacancyCountByTechnology()).isEqualTo(
                Map.ofEntries(
                        Map.entry("Java", 3L),
                        Map.entry("Spring", 2L),
                        Map.entry("Spring Data", 2L),
                        Map.entry("Spring Boot", 1L),
                        Map.entry("React", 1L),
                        Map.entry("Python", 3L),
                        Map.entry("TensorFlow", 2L),
                        Map.entry("SQL", 5L),
                        Map.entry("JavaScript", 1L),
                        Map.entry("Redux", 1L),
                        Map.entry("Vue.js", 1L),
                        Map.entry("HTML", 1L),
                        Map.entry("CSS", 1L),
                        Map.entry("C++", 3L),
                        Map.entry("Qt", 1L),
                        Map.entry("Boost", 2L),
                        Map.entry("Docker", 2L),
                        Map.entry("Kubernetes", 2L),
                        Map.entry("Jenkins", 2L),
                        Map.entry("Angular", 1L),
                        Map.entry("C#", 1L),
                        Map.entry("C", 1L),
                        Map.entry("OpenGL", 1L)));

        assertThat(new ArrayList<>(recruiterVacancyCountStatsDto.getVacancyCountByTechnology().values())).isSortedAccordingTo(Comparator.reverseOrder());
    }

    @Test
    void testCalculateVacancyStats_InvalidField() {
        assertThrows(IllegalArgumentException.class, () -> {
            vacancyStatsService.calculateVacancyStats(new File("src/test/resources/json/vacancy/vacancies.json").getAbsoluteFile(), "invalid");
        });
    }


    @Test
    void getMapExtractor_positionField_returnsPositionMap() {
        PositionVacancyStatsDto dto = new PositionVacancyStatsDto();
        dto.setVacancyCountByPosition(Map.of("Java Developer", 1L, "Python Developer", 2L));
        Function<AbstractDto, Map<?, Long>> extractor = vacancyStatsService.getMapExtractor("position");
        assertInstanceOf(Map.class, extractor.apply(dto));
    }

    @Test
    void getMapExtractor_salaryField_returnsSalaryMap() {
        SalaryVacancyStatsDto dto = new SalaryVacancyStatsDto();
        dto.setVacancyCountBySalary(Map.of(1000F, 2L, 2500F, 3L));
        Function<AbstractDto, Map<?, Long>> extractor = vacancyStatsService.getMapExtractor("salary");
        assertInstanceOf(Map.class, extractor.apply(dto));
    }

    @Test
    void getMapExtractor_recruiterField_returnsRecruiterMap() {
        RecruiterVacancyStatsDto dto = new RecruiterVacancyStatsDto();
        dto.setVacancyCountByRecruiter(Map.of(new RecruiterDto("Vladyslav", "Bondar", "ProfITsoft"), 2L, new RecruiterDto("John", "Doe", "TechCorp"), 3L));
        Function<AbstractDto, Map<?, Long>> extractor = vacancyStatsService.getMapExtractor("recruiter");
        assertInstanceOf(Map.class, extractor.apply(dto));
    }

    @Test
    void getMapExtractor_technologyStackField_returnsTechnologyStackMap() {
        TechnologyVacancyStatsDto dto = new TechnologyVacancyStatsDto();
        dto.setVacancyCountByTechnology(Map.of("Java", 2L, "Spring", 3L));
        Function<AbstractDto, Map<?, Long>> extractor = vacancyStatsService.getMapExtractor("technology_stack");
        assertInstanceOf(Map.class, extractor.apply(dto));
    }

    @Test
    void getMapExtractor_invalidField_throwsIllegalArgumentException() {
        AbstractDto dto = new TechnologyVacancyStatsDto();
        assertThrows(IllegalArgumentException.class, () -> vacancyStatsService.getMapExtractor("invalid").apply(dto));
    }

    @Test
    void sortByValue_shouldReturnSortedMap_whenInputIsUnsorted() {
        Map<String, Integer> unsortedMap = new HashMap<>();
        unsortedMap.put("A", 3);
        unsortedMap.put("B", 1);
        unsortedMap.put("C", 2);

        Map<String, Integer> expectedMap = new LinkedHashMap<>();
        expectedMap.put("A", 3);
        expectedMap.put("C", 2);
        expectedMap.put("B", 1);

        Map<String, Integer> result = vacancyStatsService.sortByValue(unsortedMap, Function.identity());

        assertThat(result).isEqualTo(expectedMap);
    }

    @Test
    void sortByValue_shouldReturnSameMap_whenInputIsAlreadySorted() {
        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        sortedMap.put("A", 3);
        sortedMap.put("B", 2);
        sortedMap.put("C", 1);

        Map<String, Integer> result = vacancyStatsService.sortByValue(sortedMap, Function.identity());

        assertThat(result).isEqualTo(sortedMap);
    }

    @Test
    void sortByValue_shouldReturnEmptyMap_whenInputIsEmpty() {
        Map<String, Integer> emptyMap = new HashMap<>();

        Map<String, Integer> result = vacancyStatsService.sortByValue(emptyMap, Function.identity());

        assertThat(result).isEmpty();
        assertThat(result).isEqualTo(emptyMap);
    }

    @Test
    void sortByValue_shouldReturnConvertedKeys_whenConversionFunctionIsProvided() {
        Map<Integer, Integer> unsortedMap = new HashMap<>();
        unsortedMap.put(3, 3);
        unsortedMap.put(1, 1);
        unsortedMap.put(2, 2);

        Map<String, Integer> expectedMap = new LinkedHashMap<>();
        expectedMap.put("3", 3);
        expectedMap.put("2", 2);
        expectedMap.put("1", 1);

        Map<String, Integer> result = vacancyStatsService.sortByValue(unsortedMap, Object::toString);

        assertThat(result).isEqualTo(expectedMap);
    }
}