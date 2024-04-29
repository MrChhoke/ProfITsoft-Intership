package org.prof.it.soft.parser;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.prof.it.soft.dto.request.RequestVacancyDto;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class is responsible for parsing JSON strings into a list of RequestVacancyDto objects.
 * It contains methods for parsing vacancies from JSON strings.
 * It uses Jackson's JsonParser to parse the JSON strings.
 *
 * @see org.prof.it.soft.dto.request.RequestVacancyDto
 * @see org.prof.it.soft.entity.Vacancy
 * @see com.fasterxml.jackson.core.JsonParser
 */
@Slf4j
@Component
@Getter
public class VacancyJsonParser {

    public static final String POSITION_FIELD = "position";
    public static final String SALARY_FIELD = "salary";
    public static final String TECHNOLOGY_STACK_FIELD = "technology_stack";
    public static final String RECRUITER_ID_FIELD = "recruiter_id";

    /**
     * The set of required fields for the vacancy object.
     * This fields must be present in the JSON string for the vacancy object to be parsed successfully.
     * Also, they cannot have null values.
     */
    public final static Set<String> REQUIRED_FIELDS = Set.of(POSITION_FIELD, RECRUITER_ID_FIELD);

    /**
     * The set of allowed fields for the vacancy object.
     * These fields are allowed to be present in the JSON string for the vacancy object to be parsed successfully.
     * <p>
     * If a field is present in the JSON string not in this set, the vacancy object will not be parsed.
     */
    public final static Set<String> ALLOWED_FIELDS = Set.of(POSITION_FIELD, SALARY_FIELD, TECHNOLOGY_STACK_FIELD, RECRUITER_ID_FIELD);

    private int successParsingCounter = 0;
    private int failedParsingCounter = 0;

    /**
     * This method parses a JSON string into a list of RequestVacancyDto objects.
     * It uses Jackson's JsonParser to parse the JSON string.
     * It returns a list of RequestVacancyDto objects.
     * If the JSON string contains a vacancy object with missing required fields,
     * the method logs a warning message and does not add the vacancy object to the list.
     * If the JSON string contains a vacancy object with fields that are not allowed,
     * the method logs a warning message and does not add the vacancy object to the list.
     *
     * @param json the JSON string to parse
     * @return a list of RequestVacancyDto objects parsed from the JSON string
     * @throws IOException if an I/O error occurs while parsing the JSON string
     */
    public List<RequestVacancyDto> parseVacancies(String json) throws IOException {
        return parseVacancies(json, null);
    }

    /**
     * This method parses a JSON string into a list of RequestVacancyDto objects.
     * It uses Jackson's JsonParser to parse the JSON string.
     * It returns a list of RequestVacancyDto objects.
     * If the JSON string contains a vacancy object with missing required fields,
     * the method logs a warning message and does not add the vacancy object to the list.
     * If the JSON string contains a vacancy object with fields that are not allowed,
     * the method logs a warning message and does not add the vacancy object to the list.
     * If the JSON string contains a vacancy object with a recruiter_id that is not in the allowedRecruiterIds set,
     * the method logs a warning message and does not add the vacancy object to the list.
     *
     * @param json                the JSON string to parse
     * @param allowedRecruiterIds the set of allowed recruiter ids
     * @return a list of RequestVacancyDto objects parsed from the JSON string
     * @throws IOException if an I/O error occurs while parsing the JSON string
     */
    public List<RequestVacancyDto> parseVacancies(String json, Set<Long> allowedRecruiterIds) throws IOException {
        List<RequestVacancyDto> vacancies = new ArrayList<>();
        RequestVacancyDto vacancy = new RequestVacancyDto();
        Set<String> currentObjectFields = new HashSet<>();

        AtomicBoolean containsOnlyAllowedFields = new AtomicBoolean(true);
        AtomicBoolean containsFieldsWithInvalidValues = new AtomicBoolean(false);
        AtomicInteger vacancyCounter = new AtomicInteger(0);

        String fieldName = null;

        successParsingCounter = 0;
        failedParsingCounter = 0;

        // Create a new JsonParser from the JSON string
        try (JsonParser jsonParser = JsonFactory.builder().build().createParser(json)) {
            while (jsonParser.nextToken() != null) {
                switch (jsonParser.getCurrentToken()) {
                    case START_OBJECT:
                        vacancy = createNewVacancyDto(currentObjectFields, containsOnlyAllowedFields, containsFieldsWithInvalidValues, vacancyCounter);
                        break;
                    case END_OBJECT:
                        validateAndAddVacancy(currentObjectFields, vacancyCounter, containsOnlyAllowedFields,
                                containsFieldsWithInvalidValues, vacancies, vacancy);
                        break;
                    case FIELD_NAME:
                        fieldName = jsonParser.getCurrentName();
                        handleFieldName(fieldName, containsOnlyAllowedFields);
                        break;
                    case VALUE_STRING:
                        String fieldValue = jsonParser.getValueAsString();

                        if (POSITION_FIELD.equals(fieldName)) {
                            handlePosition(vacancy, fieldValue, containsFieldsWithInvalidValues, vacancyCounter);
                            currentObjectFields.add(fieldName);
                        }
                        break;
                    case VALUE_NUMBER_FLOAT, VALUE_NUMBER_INT:
                        Number fieldNumValue = jsonParser.getNumberValue();

                        if (SALARY_FIELD.equals(fieldName)) {
                            handleSalary(vacancy, fieldNumValue.floatValue(), containsFieldsWithInvalidValues, vacancyCounter);
                            currentObjectFields.add(fieldName);
                        }

                        if (RECRUITER_ID_FIELD.equals(fieldName)) {
                            handleRecruiterId(vacancy, fieldNumValue, containsFieldsWithInvalidValues, vacancyCounter, allowedRecruiterIds);
                            currentObjectFields.add(fieldName);
                        }
                        break;
                    case START_ARRAY:
                        if (TECHNOLOGY_STACK_FIELD.equals(fieldName)) {
                            vacancy.setTechnologyStack(parseTechnologyStack(jsonParser));
                            currentObjectFields.add(fieldName);
                        }
                        break;
                    case VALUE_NULL:
                        handleNullValues(fieldName, vacancyCounter, containsFieldsWithInvalidValues);
                        break;
                    default:
                        break;
                }
            }
            log.info("Successfully parsed {} vacancies, failed to parse {} vacancies", successParsingCounter, failedParsingCounter);
            return vacancies;
        }
    }

    protected RequestVacancyDto createNewVacancyDto(Set<String> currentObjectFields,
                                                  AtomicBoolean containsOnlyAllowedFields,
                                                  AtomicBoolean containsFieldsWithInvalidValues,
                                                  AtomicInteger vacancyCounter) {
        currentObjectFields.clear();
        containsOnlyAllowedFields.set(true);
        containsFieldsWithInvalidValues.set(false);
        vacancyCounter.incrementAndGet();
        return new RequestVacancyDto();
    }

    protected void handleRecruiterId(RequestVacancyDto vacancy, Number fieldNumValue,
                                          AtomicBoolean containsFieldsWithInvalidValues, AtomicInteger vacancyCounter,
                                          Set<Long> allowedRecruiterIds) {
        // If the allowedRecruiterIds set is not null and the recruiter_id
        // is not in the set the vacancy object is not valid
        if (allowedRecruiterIds != null && !allowedRecruiterIds.contains(fieldNumValue.longValue())) {
            containsFieldsWithInvalidValues.set(true);
            log.warn("Vacancy object #{} contains not allowed recruiter id: {}", vacancyCounter.get(), fieldNumValue.longValue());
            return;
        }
        vacancy.setRecruiterId(fieldNumValue.longValue());
    }

    protected void handleSalary(RequestVacancyDto vacancy, Float salary,
                              AtomicBoolean containsFieldsWithInvalidValues, AtomicInteger vacancyCounter) {
        // Check if the salary is positive
        if (salary <= 0) {
            containsFieldsWithInvalidValues.set(true);
            log.warn("Vacancy object #{} contains not positive salary value: {}", vacancyCounter.get(), salary);
            return;
        }

        vacancy.setSalary(salary);
    }

    protected void handlePosition(RequestVacancyDto vacancy,
                                       String fieldValue,
                                       AtomicBoolean containsFieldsWithInvalidValues,
                                       AtomicInteger vacancyCounter) {
        if (fieldValue.isBlank()) {
            containsFieldsWithInvalidValues.set(true);
            log.warn("Vacancy object #{} contains blank position value: {}", vacancyCounter.get(), fieldValue);
            return;
        }

        vacancy.setPosition(fieldValue);
    }


    protected void handleNullValues(String fieldName, AtomicInteger vacancyCounter, AtomicBoolean containsFieldsWithInvalidValues) {
        if (REQUIRED_FIELDS.contains(fieldName)) {
            log.warn("Vacancy object #{} contains null value for required field: {}", vacancyCounter.get(), fieldName);
            containsFieldsWithInvalidValues.set(true);
        }
    }

    /**
     * This method validates the vacancy object and adds it to the list of vacancies if it is valid.
     * If the vacancy object is not valid, the method logs a warning message.
     *
     * @param currentObjectFields             the set of fields in the vacancy object
     * @param vacancyCounter                  the counter of the vacancy objects
     * @param containsOnlyAllowedFields       the flag indicating if the vacancy object contains only allowed fields
     * @param containsFieldsWithInvalidValues the flag indicating if the vacancy object contains fields with invalid values
     * @param vacancies                       the list of vacancies
     * @param vacancy                         the vacancy object to validate and add
     */
    protected void validateAndAddVacancy(Set<String> currentObjectFields, AtomicInteger vacancyCounter,
                                       AtomicBoolean containsOnlyAllowedFields, AtomicBoolean containsFieldsWithInvalidValues,
                                       List<RequestVacancyDto> vacancies, RequestVacancyDto vacancy) {
        if (!currentObjectFields.containsAll(REQUIRED_FIELDS)) {
            Set<String> missingFields = new HashSet<>(REQUIRED_FIELDS);
            missingFields.removeAll(currentObjectFields);
            log.warn("Vacancy object #{} is missing required fields: {}", vacancyCounter.get(), missingFields);
            failedParsingCounter++;
            return;
        }

        if (!containsOnlyAllowedFields.get()) {
            Set<String> invalidFields = new HashSet<>(currentObjectFields);
            invalidFields.removeAll(ALLOWED_FIELDS);
            log.warn("Vacancy object #{} contains fields that are not allowed: {}", vacancyCounter.get(), invalidFields);
            failedParsingCounter++;
            return;
        }

        if (containsFieldsWithInvalidValues.get()) {
            log.warn("Vacancy object #{} contains fields with invalid values", vacancyCounter.get());
            failedParsingCounter++;
            return;
        }

        successParsingCounter++;
        vacancies.add(vacancy);
    }

    /**
     * This method handles the field name in the JSON string.
     * It checks if the field name is allowed and logs a warning message if it is not.
     *
     * @param containsOnlyAllowedFields the flag indicating if the vacancy object contains only allowed fields
     * @return the field name
     */
    protected void handleFieldName(String fieldName, AtomicBoolean containsOnlyAllowedFields) {
        if (!ALLOWED_FIELDS.contains(fieldName)) {
            containsOnlyAllowedFields.set(false);
            log.warn("Field '{}' is not allowed", fieldName);
        }
    }

    /**
     * This method parses the technology stack array in the JSON string.
     * It returns a list of technology stack strings.
     *
     * @param jsonParser the JsonParser object
     * @return a list of technology stack strings
     * @throws IOException if an I/O error occurs while parsing the JSON string
     */
    protected List<String> parseTechnologyStack(JsonParser jsonParser) throws IOException {
        List<String> technologyStack = new ArrayList<>();
        while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
            technologyStack.add(jsonParser.getValueAsString());
        }
        return technologyStack;
    }

}
