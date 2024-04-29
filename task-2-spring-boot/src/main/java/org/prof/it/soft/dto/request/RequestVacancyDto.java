package org.prof.it.soft.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public final class RequestVacancyDto {

    /**
     * The position of the vacancy.
     */
    @JsonProperty(value = "position")
    @NotNull(groups = {Save.class, Update.class}, message = "Position is required")
    @NotBlank(groups = {Save.class, Update.class}, message = "Position is required")
    private String position;

    /**
     * The salary of the vacancy.
     */
    @JsonProperty(value = "salary")
    @Positive(groups = {Save.class, Update.class}, message = "Salary must be greater than 0")
    private Float salary;

    /**
     * The company name of the vacancy.
     */
    @JsonProperty(value = "technology_stack")
    private List<String> technologyStack;

    /**
     * The id of the recruiter associated with the vacancy.
     */
    @JsonProperty(value = "recruiter_id")
    @NotNull(groups = {Save.class, Update.class}, message = "Recruiter id is required")
    private Long recruiterId;

    public interface Save {
    }

    public interface Update {
    }
}
