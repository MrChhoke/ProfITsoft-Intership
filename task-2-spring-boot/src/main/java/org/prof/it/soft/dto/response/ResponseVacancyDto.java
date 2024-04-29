package org.prof.it.soft.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "vacancy_id",
        "position",
        "salary",
        "technology_stack",
        "created_at",
        "updated_at",
        "recruiter"
})
@NoArgsConstructor
@AllArgsConstructor
public final class ResponseVacancyDto {

    /**
     * The vacancy's id.
     */
    @JsonProperty("vacancy_id")
    private Long id;

    /**
     * The vacancy's position.
     */
    @JsonProperty("position")
    private String position;

    /**
     * The vacancy's salary.
     */
    @JsonProperty("salary")
    private Float salary;

    /**
     * The vacancy's technology stack.
     */
    @JsonProperty("technology_stack")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> technologyStack;

    /**
     * The date and time when the vacancy was created.
     */
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    /**
     * The date and time when the vacancy was updated.
     */
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    /**
     * The recruiter associated with the vacancy.
     *
     * @see org.prof.it.soft.dto.response.ResponseRecruiterDto
     */
    @JsonProperty("recruiter")
    private ResponseRecruiterDto recruiter;
}
