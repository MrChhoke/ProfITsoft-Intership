package org.prof.it.soft.dto.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object (DTO) for filtering vacancies.
 * It includes fields like recruiter id, position, salary range, company name,
 * technology stack, creation date range, page number, and page size.
 */
@Data
public class VacancyFilterDto {

    /**
     * The id of the recruiter.
     */
    @JsonProperty("recruiter_id")
    private final Long recruiterId;

    /**
     * The position of the vacancy.
     */
    @JsonProperty("position")
    private final String position;

    /**
     * The minimum salary of the vacancy.
     */
    @JsonProperty("min_salary")
    private final Float minSalary;

    /**
     * The maximum salary of the vacancy.
     */
    @JsonProperty("max_salary")
    private final Float maxSalary;

    /**
     * The name of the company.
     */
    @JsonProperty("company_name")
    private final String companyName;

    /**
     * The technology stack of the vacancy.
     */
    @JsonProperty("technology_stack")
    private final List<String> technologyStack;

    /**
     * The minimum creation date of the vacancy.
     */
    @JsonProperty("created_at_min")
    private final LocalDateTime createdAtMin;

    /**
     * The maximum creation date of the vacancy.
     */
    @JsonProperty("created_at_max")
    private final LocalDateTime createdAtMax;

    /**
     * The page number for pagination.
     * It is required for JsonResponse.
     */
    @JsonProperty("page")
    @NotNull(message = "Page number is required", groups = JsonResponse.class)
    private Integer page;

    /**
     * The page size for pagination.
     * It is required for JsonResponse.
     */
    @JsonProperty("size")
    @NotNull(message = "Page size is required", groups = JsonResponse.class)
    private Integer size;

    /**
     * Interface for JsonResponse.
     */
    public interface JsonResponse {
    }

    /**
     * Interface for ExcelResponse.
     */
    public interface ExcelResponse {
    }
}