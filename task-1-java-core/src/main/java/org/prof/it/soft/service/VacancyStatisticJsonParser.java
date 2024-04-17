package org.prof.it.soft.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.prof.it.soft.dto.RecruiterDto;

import java.io.IOException;
import java.util.*;

/**
 * This class is responsible for parsing JSON files that contain vacancy data and calculating statistics based on a specified field.
 * The statistics are calculated by counting the occurrences of unique values for the specified field.
 *
 * <b>This class doesn't return a list of Vacancy objects, but instead calculates statistics based on the specified field.</b>
 *
 * The class uses the Jackson library to parse the JSON data.
 */
@RequiredArgsConstructor
public class VacancyStatisticJsonParser {

    /**
     * A set of required fields for each JSON object.
     */
    public final static Set<String> requiredFields = Set.of("position", "recruiter_first_name");

    /**
     * The JsonParser used to parse the JSON data.
     */
    protected final JsonParser jsonParser;

    /**
     * Processes the JSON file and calculates the statistics based on the specified statistic field.
     * The statistics are calculated by counting the occurrences of unique values for the specified field.
     * The method returns a map where the keys are the unique values for the specified field and the values are the number of occurrences.
     *
     * This method processes the JSON file token by token and updates the statistics map based on the statistic field and the current value.
     * The approach can save on memory usage by processing the JSON file incrementally and not loading the entire file into memory.
     *
     * Use this method to process large JSON files that contain vacancy data and calculate statistics based on a specified field.
     *
     * @param statisticField the field to calculate the statistics for
     * @return a map containing the statistics
     * @throws IOException if an I/O error occurs
     */
    public Map<Object, Long> processJsonFile(@NonNull String statisticField) throws IOException {
        Map<Object, Long> statisticMap = new HashMap<>();

        // Initialize a set to store the current object's required fields
        Set<String> currentObjectRequiredFields = new HashSet<>();
        // Initialize variables to store the current field, recruiter details, and current value
        String currentField = null;
        String recruiterFirstName = null;
        String recruiterLastName = null;
        String recruiterCompanyName = null;
        Object currentValue = null;

        JsonToken jsonToken;
        // Loop through the JSON tokens until the end of the file
        while ((jsonToken = jsonParser.nextToken()) != null) {
            switch (jsonToken) {
                case START_OBJECT:
                    // Clear the current object's required fields and reset recruiter details
                    currentObjectRequiredFields.clear();
                    recruiterFirstName = null;
                    recruiterLastName = null;
                    recruiterCompanyName = null;
                    break;
                case END_OBJECT:
                    // If the current object contains all required fields, process the end object
                    if (requiredFields.equals(currentObjectRequiredFields)) {
                        processEndObject(statisticMap,
                                statisticField,
                                recruiterFirstName,
                                recruiterLastName,
                                recruiterCompanyName,
                                currentValue);
                    }
                    // Reset the current value
                    currentValue = null;
                    break;
                case FIELD_NAME:
                    // Store the current field name
                    currentField = jsonParser.getText();
                    break;
                case VALUE_STRING:
                    // If the current field is a required field, add it to the current object's required fields
                    if (requiredFields.contains(currentField)) {
                        currentObjectRequiredFields.add(currentField);
                    }

                    String value = jsonParser.getValueAsString();
                    // Update the current value or recruiter details based on the statistic field and current field
                    if (value != null) {
                        if (statisticField.equals(currentField)) {
                            currentValue = value;
                        } else if ("recruiter".equals(statisticField) && currentField != null) {
                            switch (currentField) {
                                case "recruiter_first_name" -> recruiterFirstName = value;
                                case "recruiter_last_name" -> recruiterLastName = value;
                                case "recruiter_company_name" -> recruiterCompanyName = value;
                            }
                        }
                    }

                    break;
                case VALUE_NUMBER_FLOAT, VALUE_NUMBER_INT:
                    // If the statistic field matches the current field, update the current value with the number
                    if (Objects.equals(statisticField, currentField)) {
                        double num = jsonParser.getValueAsDouble();

                        currentValue = String.valueOf(num);

                        // If the statistic field is "salary" and the number is negative, ignore the current value
                        if ("salary".equals(statisticField) && num < 0) {
                            currentValue = null;
                        }
                    }
            }
        }

        return statisticMap;
    }


    /**
     * Processes the end of a JSON object and updates the statistics map based on the statistic field and the current value.
     * <p>
     * If the statistic field is "recruiter", a RecruiterDto object is created and used as the key to increment the value in the statistics map.
     * If the statistic field is "technology_stack" and the current value is not null, the current value is split into technologies and each technology is used as the key to increment the value in the statistics map.
     * If the current value is not null and the statistic field is neither "recruiter" nor "technology_stack", the current value is used as the key to increment the value in the statistics map.
     *
     * @param statisticMap         the map to update with the statistics
     * @param statisticField       the statistic field to calculate
     * @param recruiterFirstName   the first name of the recruiter
     * @param recruiterLastName    the last name of the recruiter
     * @param recruiterCompanyName the company name of the recruiter
     * @param currentValue         the current value of the statistic field
     */
    protected void processEndObject(Map<Object, Long> statisticMap,
                                    String statisticField,
                                    String recruiterFirstName,
                                    String recruiterLastName,
                                    String recruiterCompanyName,
                                    Object currentValue) {
        if ("recruiter".equals(statisticField)) {
            RecruiterDto recruiterDto = RecruiterDto.builder()
                    .firstName(recruiterFirstName)
                    .lastName(recruiterLastName)
                    .companyName(recruiterCompanyName)
                    .build();
            incrementValue(statisticMap, recruiterDto);
        } else if ("technology_stack".equals(statisticField) && currentValue != null) {
            for (String tech : ((String) currentValue).split(",\\s+")) {
                incrementValue(statisticMap, tech);
            }
        } else if (currentValue != null) {
            incrementValue(statisticMap, currentValue);
        }
    }

    /**
     * Increment value in statistic map
     *
     * @param statisticMap map to increment value
     * @param key key to increment
     */
    protected void incrementValue(Map<Object, Long> statisticMap, Object key) {
        statisticMap.put(key, statisticMap.getOrDefault(key, 0L) + 1);
    }

}
