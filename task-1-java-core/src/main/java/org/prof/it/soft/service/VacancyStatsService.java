package org.prof.it.soft.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.prof.it.soft.config.Configuration;
import org.prof.it.soft.dto.AbstractDto;
import org.prof.it.soft.dto.RecruiterDto;
import org.prof.it.soft.dto.VacancyDto;
import org.prof.it.soft.dto.stats.PositionVacancyStatsDto;
import org.prof.it.soft.dto.stats.RecruiterVacancyStatsDto;
import org.prof.it.soft.dto.stats.SalaryVacancyStatsDto;
import org.prof.it.soft.dto.stats.TechnologyVacancyStatsDto;
import org.prof.it.soft.entity.Vacancy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

/**
 * This class provides methods to calculate various statistics related to vacancies.
 */
@Slf4j
public class VacancyStatsService {

    /**
     * A set of allowed statistic fields.
     */
    public final static Set<String> allowedStatisticFields = Set.of("position", "salary", "recruiter", "technology_stack");

    /**
     * The default number of threads to use for calculations in parallel mode.
     */
    public final static int DEFAULT_COUNT_THREADS = 4;

    /**
     * Calculates salary statistics for a list of vacancies.
     *
     * @param vacancies the list of vacancies
     * @return a SalaryVacancyStatsDto object containing the calculated statistics
     */
    public SalaryVacancyStatsDto calculateSalaryVacancyStats(@NonNull List<Vacancy> vacancies) {
        Map<Float, Long> statisticMap = vacancies.stream()
                .filter(vacancy -> vacancy.getSalary() != null)
                .collect(Collectors.groupingBy(Vacancy::getSalary, Collectors.counting()));

        Map<Float, Long> map = sortByValue(statisticMap);

        // Calculate the average, maximum, and minimum salary, and return the statistics
        DoubleSummaryStatistics stats = map.entrySet().stream()
                .flatMapToDouble(entry -> DoubleStream.generate(entry.getKey()::doubleValue)
                        .limit(entry.getValue()))
                .summaryStatistics();

        return SalaryVacancyStatsDto
                .builder()
                .vacancyCountBySalary(map)
                .averageSalary(stats.getAverage())
                .maxSalary(stats.getMax())
                .minSalary(stats.getMin())
                .build();
    }

    /**
     * Groups a list of items by a classifier function and counts the number of items in each group.
     *
     * @param list       the list of items
     * @param classifier the classifier function
     * @return a map where the keys are the group identifiers and the values are the counts
     */
    protected <T, K> Map<K, Long> groupAndCount(List<T> list, Function<T, K> classifier) {
        return list.stream()
                .collect(Collectors.groupingBy(classifier, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new));
    }

    /**
     * Calculates technology stack statistics for a list of vacancies.
     *
     * @param vacancies the list of vacancies
     * @return a TechnologyVacancyStatsDto object containing the calculated statistics
     */
    public TechnologyVacancyStatsDto calculateTechnologyVacancyStats(@NonNull List<Vacancy> vacancies) {
        List<String> vacancyCountByTechnologyStack = vacancies.stream()
                .flatMap(vacancy -> vacancy.getTechnologyStack().stream())
                .toList();

        return TechnologyVacancyStatsDto.builder()
                .vacancyCountByTechnology(groupAndCount(vacancyCountByTechnologyStack, String::toString))
                .build();
    }

    /**
     * Calculates recruiter statistics for a list of vacancies.
     *
     * @param vacancies the list of vacancies
     * @return a RecruiterVacancyStatsDto object containing the calculated statistics
     */
    public RecruiterVacancyStatsDto calculateRecruiterVacancyCountStats(@NonNull List<Vacancy> vacancies) {
        List<VacancyDto> vacancyDtos = vacancies.stream()
                .map(vacancy -> Configuration.getModelMapper().map(vacancy, VacancyDto.class))
                .collect(Collectors.toList());

        return RecruiterVacancyStatsDto.builder()
                .vacancyCountByRecruiter(groupAndCount(vacancyDtos, VacancyDto::getRecruiterDto))
                .build();
    }

    /**
     * Calculates position statistics for a list of vacancies.
     *
     * @param vacancies the list of vacancies
     * @return a PositionVacancyStatsDto object containing the calculated statistics
     */
    public PositionVacancyStatsDto calculatePositionVacancyStats(@NonNull List<Vacancy> vacancies) {
        return PositionVacancyStatsDto.builder()
                .vacancyCountByPosition(groupAndCount(vacancies, Vacancy::getPosition))
                .build();
    }


    /**
     * Calculates vacancy statistics for a directory of JSON files.
     * The statistics are calculated by counting the occurrences of unique values for the specified field.
     * The statistics are calculated using the default number of threads.
     *
     * @param folderPath     the path to the directory
     * @param statisticField the statistic field to calculate
     * @return an AbstractDto object containing the calculated statistics
     * @throws IOException if an I/O error occurs
     */
    public AbstractDto calculateVacancyStats(Path folderPath, String statisticField) throws IOException {
        return calculateVacancyStats(folderPath, statisticField, DEFAULT_COUNT_THREADS);
    }


    /**
     * Calculates vacancy statistics for a directory of JSON files using a specified number of threads.
     * The statistics are calculated by counting the occurrences of unique values for the specified field.
     *
     * @param folderPath     the path to the directory
     * @param statisticField the statistic field to calculate
     * @param countThreads   the number of threads to use
     * @return an AbstractDto object containing the calculated statistics
     * @throws IOException if an I/O error occurs
     */
    protected AbstractDto calculateVacancyStats(Path folderPath, String statisticField, int countThreads) throws IOException {
        // If statistic field is not allowed, throw an exception
        if (!allowedStatisticFields.contains(statisticField)) {
            throw new IllegalArgumentException("Invalid statistic field");
        }

        List<File> jsonFiles;

        // Get a list of JSON files in the directory
        try (Stream<Path> jsonPaths = Files.list(folderPath).filter(path -> path.toString().endsWith(".json"))) {
            jsonFiles = jsonPaths.map(Path::toFile).toList();
        }

        // Create a fixed thread pool executor with the specified number of threads
        ExecutorService executor = Executors.newFixedThreadPool(countThreads);

        // Create a list of CompletableFuture objects for each JSON file
        List<CompletableFuture<AbstractDto>> futures = jsonFiles.stream()
                .map(file -> CompletableFuture.supplyAsync(() -> {
                    VacancyStatsService vacancyStatsService = new VacancyStatsService();
                    try {
                        return vacancyStatsService.calculateVacancyStats(file, statisticField);
                    } catch (IOException e) {
                        log.error("Error processing file: {}", file, e);
                        return null;
                    }
                }, executor))
                .toList();

        // Join the futures to get the list of AbstractDto objects
        List<AbstractDto> abstractDtos = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        // Shutdown the executor
        executor.shutdown();

        var mapExtractor = getMapExtractor(statisticField);
        Map<Object, Long> mapStatistic = abstractDtos.stream()
                .map(mapExtractor)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        Long::sum));

        return generateStatisticDto(mapStatistic, statisticField);
    }

    /**
     * Returns a function that extracts a map of statistics from an AbstractDto object based on a statistic field.
     *
     * @param statisticField the statistic field
     * @return a function that extracts a map of statistics from an AbstractDto object
     * @throws IllegalArgumentException if the statistic field is invalid
     */
    protected Function<AbstractDto, Map<?, Long>> getMapExtractor(String statisticField) {
        return switch (statisticField) {
            case "position" -> dto -> ((PositionVacancyStatsDto) dto).getVacancyCountByPosition();
            case "salary" -> dto -> ((SalaryVacancyStatsDto) dto).getVacancyCountBySalary();
            case "recruiter" -> dto -> ((RecruiterVacancyStatsDto) dto).getVacancyCountByRecruiter();
            case "technology_stack" -> dto -> ((TechnologyVacancyStatsDto) dto).getVacancyCountByTechnology();
            default -> throw new IllegalArgumentException("Invalid statistic field");
        };
    }


    /**
     * Calculates vacancy statistics for a JSON file.
     *
     * @param jsonFile       the JSON file
     * @param statisticField the statistic field to calculate
     * @return an AbstractDto object containing the calculated statistics
     * @throws IllegalArgumentException if the statistic field is invalid
     * @throws IOException              if an I/O error occurs
     */
    protected AbstractDto calculateVacancyStats(File jsonFile, String statisticField) throws IOException {
        if (statisticField == null || !allowedStatisticFields.contains(statisticField)) {
            throw new IllegalArgumentException("Invalid statistic field");
        }

        Map<Object, Long> mapStatistic;
        ObjectMapper mapper = new ObjectMapper();

        try (JsonParser jsonParser = mapper.createParser(new BufferedReader(new FileReader(jsonFile)))) {
            VacancyStatisticJsonParser vacancyStatisticJsonParser = new VacancyStatisticJsonParser(jsonParser);
            mapStatistic = vacancyStatisticJsonParser.processJsonFile(statisticField);
        }

        return generateStatisticDto(mapStatistic, statisticField);
    }

    /**
     * Generates a statistic DTO based on a map of statistics and a statistic field.
     *
     * @param statisticMap   the map of statistics
     * @param statisticField the statistic field
     * @return an AbstractDto object containing the statistics
     */
    protected AbstractDto generateStatisticDto(Map<Object, Long> statisticMap, String statisticField) {
        return switch (statisticField) {
            case "position" -> generatePositionDto(statisticMap);
            case "salary" -> generateSalaryDto(statisticMap);
            case "recruiter" -> generateRecruiterDto(statisticMap);
            case "technology_stack" -> generateTechnologyStackDto(statisticMap);
            default -> null;
        };
    }

    /**
     * Generates a PositionVacancyStatsDto object based on a map of position statistics.
     *
     * @param statisticMap the map of position statistics
     * @return a PositionVacancyStatsDto object containing the statistics
     */
    protected PositionVacancyStatsDto generatePositionDto(Map<Object, Long> statisticMap) {
        return PositionVacancyStatsDto.builder()
                .vacancyCountByPosition(sortByValue(statisticMap, Object::toString))
                .build();
    }

    /**
     * Generates a SalaryVacancyStatsDto object based on a map of salary statistics.
     *
     * @param statisticMap the map of salary statistics
     * @return a SalaryVacancyStatsDto object containing the statistics
     */
    protected SalaryVacancyStatsDto generateSalaryDto(Map<Object, Long> statisticMap) {
        Map<Float, Long> map = sortByValue(statisticMap, key -> Float.valueOf(key.toString()));

        DoubleSummaryStatistics stats = map.entrySet().stream()
                .flatMapToDouble(entry -> DoubleStream.generate(entry.getKey()::doubleValue)
                        .limit(entry.getValue()))
                .summaryStatistics();

        return SalaryVacancyStatsDto
                .builder()
                .vacancyCountBySalary(map)
                .averageSalary(stats.getAverage())
                .maxSalary(stats.getMax())
                .minSalary(stats.getMin())
                .build();
    }

    /**
     * Generates a RecruiterVacancyStatsDto object based on a map of recruiter statistics.
     *
     * @param statisticMap the map of recruiter statistics
     * @return a RecruiterVacancyStatsDto object containing the statistics
     */
    protected RecruiterVacancyStatsDto generateRecruiterDto(Map<Object, Long> statisticMap) {
        Map<RecruiterDto, Long> map = sortByValue(statisticMap, key -> (RecruiterDto) key);

        return RecruiterVacancyStatsDto.builder()
                .vacancyCountByRecruiter(map)
                .build();
    }

    /**
     * Generates a TechnologyVacancyStatsDto object based on a map of technology statistics.
     *
     * @param statisticMap the map of technology statistics
     * @return a TechnologyVacancyStatsDto object containing the statistics
     */
    protected TechnologyVacancyStatsDto generateTechnologyStackDto(Map<Object, Long> statisticMap) {
        Map<String, Long> map = sortByValue(statisticMap, Object::toString);

        return TechnologyVacancyStatsDto.builder()
                .vacancyCountByTechnology(map)
                .build();
    }

    /**
     * Sorts a map by its values in descending order.
     * The keys are not converted.
     *
     * @param map the map to sort
     * @return a new map containing the same entries as the input map, sorted by value in descending order
     */
    protected <K, V extends Comparable<? super V>, D> Map<K, V> sortByValue(Map<D, V> map) {
        @SuppressWarnings("unchecked") Map<K, V> result = (Map<K, V>) sortByValue(map, Function.identity());
        return result;
    }

    /**
     * Sorts a map by its values in descending order and converts the keys using a conversion function.
     *
     * @param map                the map to sort
     * @param conversionFunction the function to convert the keys
     * @return a new map containing the same entries as the input map, sorted by value in descending order and with the keys converted
     */
    protected <K, V extends Comparable<? super V>, D> Map<K, V> sortByValue(Map<D, V> map, Function<D, K> conversionFunction) {
        return map.entrySet().stream()
                // Convert the keys using the conversion function
                .map(entry -> Map.entry(conversionFunction.apply(entry.getKey()), entry.getValue()))
                // Sort the entries by value in descending order
                .sorted(Map.Entry.<K, V>comparingByValue().reversed())
                // Collect the entries into a new map
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> newValue,
                        LinkedHashMap::new
                ));
    }
}
