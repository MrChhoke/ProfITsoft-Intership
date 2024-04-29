package org.prof.it.soft.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
public final class RequestRecruiterDto {

    /**
     * The name of the company the recruiter works for.
     */
    @JsonProperty(value = "company_name")
    private String companyName;

    /**
     * The person object associated with the recruiter.
     */
    @JsonUnwrapped
    @NotNull(groups = {Save.class})
    @Valid
    private RequestPersonDto person;

    /**
     * This method checks if the recruiter DTO is empty.
     * It returns true if both the first name and last name of the person object are not present.
     */
    @AssertTrue(groups = {Update.class})
    public boolean isEmptyRequestRecruiterDto() {
        return !StringUtils.hasText(person.getFirstName()) &&
                !StringUtils.hasText(person.getLastName());
    }

    /**
     * This interface is used as a group for validation constraints.
     * It is used when saving a recruiter.
     */
    public interface Save {
    }

    /**
     * This interface is used as a group for validation constraints.
     * It is used when updating a recruiter.
     */
    public interface Update {
    }

}