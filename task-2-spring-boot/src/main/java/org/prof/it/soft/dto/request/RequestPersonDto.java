package org.prof.it.soft.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public final class RequestPersonDto {

    /**
     * The first name of the person.
     */
    @JsonProperty(value = "first_name")
    @NotNull(groups = {Save.class, Update.class})
    @NotBlank(groups = {Save.class, Update.class})
    private String firstName;

    /**
     * The last name of the person.
     */
    @JsonProperty(value = "last_name")
    private String lastName;

    /**
     * This interface is used as a group for validation constraints.
     */
    public interface Save {
    }

    /**
     * This interface is used as a group for validation constraints.
     */
    public interface Update {
    }
}