package org.prof.it.soft.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.prof.it.soft.config.Configuration;
import org.prof.it.soft.dto.VacancyDto;
import org.prof.it.soft.dto.stats.PositionVacancyStatsDto;
import org.prof.it.soft.dto.stats.RecruiterVacancyStatsDto;
import org.prof.it.soft.dto.stats.SalaryVacancyStatsDto;
import org.prof.it.soft.dto.stats.TechnologyVacancyStatsDto;
import org.prof.it.soft.entity.Vacancy;
import org.prof.it.soft.generator.VacancyDtoGenerator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class VacancyStatsServiceParallelTest {

    protected static final Integer NUM_FILES = 157;
    protected static final Integer NUM_RECORD_JSON = 27673;
    protected static final File TEMP_VACANCY_JSON_DIR = new File("src/test/resources/json/vacancy/temp");

    protected static VacancyStatsService vacancyStatsService = new VacancyStatsService();

    protected static PositionVacancyStatsDto expectedPositionVacancyStatsDto;
    protected static SalaryVacancyStatsDto expectedSalaryVacancyStatsDto;
    protected static RecruiterVacancyStatsDto expectedRecruiterVacancyStatsDto;
    protected static TechnologyVacancyStatsDto expectedTechnologyVacancyStatsDto;

    @BeforeAll
    public static void createTestFiles() {
        // Clean up the directory
        TEMP_VACANCY_JSON_DIR.mkdirs();
        File[] files = TEMP_VACANCY_JSON_DIR.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }


        List<PositionVacancyStatsDto> positionVacancyStatsDtos = new CopyOnWriteArrayList<>();
        List<SalaryVacancyStatsDto> salaryVacancyStatsDtos = new CopyOnWriteArrayList<>();
        List<RecruiterVacancyStatsDto> recruiterVacancyStatsDtos = new CopyOnWriteArrayList<>();
        List<TechnologyVacancyStatsDto> technologyVacancyStatsDtos = new CopyOnWriteArrayList<>();

        // Generate DTOs and save to files in parallel
        IntStream.range(1, NUM_FILES).parallel().forEach(i -> {
            VacancyDtoGenerator vacancyDtoGenerator = new VacancyDtoGenerator();
            DtoSerializer dtoSerializer = new DtoSerializer();

            // Generate DTOs
            List<VacancyDto> generatedDtos = vacancyDtoGenerator.generateVacancyDtos(NUM_RECORD_JSON);

            List<Vacancy> vacancies = generatedDtos.stream()
                    .map(dto -> Configuration.getModelMapper().map(dto, Vacancy.class))
                    .toList();

            // Calculate position stats
            PositionVacancyStatsDto positionVacancyStatsDto = vacancyStatsService
                    .calculatePositionVacancyStats(vacancies);
            positionVacancyStatsDtos.add(positionVacancyStatsDto);

            // Calculate salary stats
            SalaryVacancyStatsDto salaryVacancyStatsDto = vacancyStatsService
                    .calculateSalaryVacancyStats(vacancies);
            salaryVacancyStatsDtos.add(salaryVacancyStatsDto);

            // Calculate recruiter stats
            RecruiterVacancyStatsDto recruiterVacancyStatsDto = vacancyStatsService
                    .calculateRecruiterVacancyCountStats(vacancies);
            recruiterVacancyStatsDtos.add(recruiterVacancyStatsDto);

            // Calculate technology stats
            TechnologyVacancyStatsDto technologyVacancyStatsDto = vacancyStatsService
                    .calculateTechnologyVacancyStats(vacancies);
            technologyVacancyStatsDtos.add(technologyVacancyStatsDto);


            // Save to file
            Path filePath = Path.of(TEMP_VACANCY_JSON_DIR.getAbsolutePath()).resolve("vacancies_" + i + ".json");
            try {
                dtoSerializer.collectionObjectToJsonFile(generatedDtos, filePath.toFile());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        expectedPositionVacancyStatsDto = PositionVacancyStatsDto.builder()
                .vacancyCountByPosition(buildVacancyStatsMap(positionVacancyStatsDtos,
                        PositionVacancyStatsDto::getVacancyCountByPosition))
                .build();

        expectedSalaryVacancyStatsDto = SalaryVacancyStatsDto.builder()
                .minSalary(salaryVacancyStatsDtos.stream().map(SalaryVacancyStatsDto::getMinSalary).min(Double::compareTo).orElse(0.0))
                .maxSalary(salaryVacancyStatsDtos.stream().map(SalaryVacancyStatsDto::getMaxSalary).max(Double::compareTo).orElse(0.0))
                .averageSalary(salaryVacancyStatsDtos.stream().mapToDouble(SalaryVacancyStatsDto::getAverageSalary).average().orElse(0.0))
                .vacancyCountBySalary(buildVacancyStatsMap(salaryVacancyStatsDtos,
                        SalaryVacancyStatsDto::getVacancyCountBySalary))
                .build();

        expectedRecruiterVacancyStatsDto = RecruiterVacancyStatsDto.builder()
                .vacancyCountByRecruiter(buildVacancyStatsMap(recruiterVacancyStatsDtos,
                        RecruiterVacancyStatsDto::getVacancyCountByRecruiter))
                .build();


        expectedTechnologyVacancyStatsDto = TechnologyVacancyStatsDto.builder()
                .vacancyCountByTechnology(buildVacancyStatsMap(technologyVacancyStatsDtos,
                        TechnologyVacancyStatsDto::getVacancyCountByTechnology))
                .build();

    }

    private static <T, L> Map<T, Long> buildVacancyStatsMap(List<L> technologyVacancyStatsDtos,
                                                            Function<L, Map<T, Long>> mapper) {
        return technologyVacancyStatsDtos.stream()
                .map(mapper) // Apply the mapper function to each element
                .flatMap(map -> map.entrySet().stream()) // Flatten the stream of Maps to a stream of Entry objects
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        Long::sum));
    }


    @Test
    void calculateVacancyStats_shouldProcessAllKindStats_singleThread() throws IOException {
        testWithThreads(1);
    }

    @Test
    void calculateVacancyStats_shouldProcessAllKindStats_TwoThreads() throws IOException {
        testWithThreads(2);
    }

    @Test
    void calculateVacancyStats_shouldProcessAllKindStats_FourThreads() throws IOException {
        testWithThreads(4);
    }

    @Test
    void calculateVacancyStats_shouldProcessAllKindStats_EightThreads() throws IOException {
        testWithThreads(8);
    }

    private void testWithThreads(int countThreads) throws IOException {
        measureTime("Calculate position stats", () -> {
            // Calculate position stats
            PositionVacancyStatsDto positionVacancyStatsDto = (PositionVacancyStatsDto) vacancyStatsService
                    .calculateVacancyStats(Path.of(TEMP_VACANCY_JSON_DIR.getAbsolutePath()),
                            "position", countThreads);

            assertThat(positionVacancyStatsDto).isEqualTo(expectedPositionVacancyStatsDto);
        });

        measureTime("Calculate salary stats", () -> {
            // Calculate salary stats
            SalaryVacancyStatsDto salaryVacancyStatsDto = (SalaryVacancyStatsDto) vacancyStatsService
                    .calculateVacancyStats(Path.of(TEMP_VACANCY_JSON_DIR.getAbsolutePath()),
                            "salary", countThreads);

            assertThat(salaryVacancyStatsDto).isEqualTo(expectedSalaryVacancyStatsDto);
        });


        measureTime("Calculate recruiter stats", () -> {
            // Calculate recruiter stats
            RecruiterVacancyStatsDto recruiterVacancyStatsDto = (RecruiterVacancyStatsDto) vacancyStatsService
                    .calculateVacancyStats(Path.of(TEMP_VACANCY_JSON_DIR.getAbsolutePath()),
                            "recruiter", countThreads);

            assertThat(recruiterVacancyStatsDto).isEqualTo(expectedRecruiterVacancyStatsDto);
        });


        measureTime("Calculate technology stats", () -> {
            // Calculate technology stats
            TechnologyVacancyStatsDto technologyVacancyStatsDto = (TechnologyVacancyStatsDto) vacancyStatsService
                    .calculateVacancyStats(Path.of(TEMP_VACANCY_JSON_DIR.getAbsolutePath()),
                            "technology_stack", countThreads);

            assertThat(technologyVacancyStatsDto).isEqualTo(expectedTechnologyVacancyStatsDto);
        });
    }

    private static void measureTime(String sectionName, CustomRunnable codeBlock) throws IOException {
        long startTime = System.currentTimeMillis();
        codeBlock.run();
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println(sectionName + ": " + elapsedTime + " ms");
    }

    private interface CustomRunnable {
        void run() throws IOException;
    }
}
