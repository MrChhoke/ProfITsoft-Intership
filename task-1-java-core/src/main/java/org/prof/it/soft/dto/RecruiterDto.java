package org.prof.it.soft.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a DTO (Data Transfer Object) for a recruiter.
 * It extends the AbstractDto class and includes fields for the recruiter's first name, last name, and company name.
 *
 * The class is annotated with Jackson annotations to control its serialization to JSON.
 * The `@JsonProperty` annotation sets the name and order of the JSON properties.
 * The `@JsonIgnore` annotation prevents the unknownProperties map from being included in the JSON.
 * The `@JsonAnySetter` annotation allows any unknown properties in the JSON to be added to the unknownProperties map.
 *
 * The class is also annotated with Lombok annotations to automatically generate getters, setters,
 * a builder, and equals/hashCode methods.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class RecruiterDto extends AbstractDto {

    /**
     * This field represents the recruiter's first name.
     */
    @JsonProperty(value = "first_name", index = 1, required = true)
    protected String firstName;

    /**
     * This field represents the recruiter's last name.
     */
    @JsonProperty(value = "last_name", index = 2, required = false)
    protected String lastName;

    /**
     * This field represents the recruiter's company name.
     */
    @JsonProperty(value = "company_name", index = 3, required = false)
    protected String companyName;

    /**
     * This field represents a map of unknown properties.
     * Any properties in the JSON that are not mapped to other fields in this class will be added to this map.
     */
    @JsonIgnore
    @Builder.Default
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    protected Map<String, Object> unknownProperties = new HashMap<>();

    public RecruiterDto(String firstName, String lastName, String companyName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.companyName = companyName;
    }

    /**
     * This method allows any unknown properties in the JSON to be added to the unknownProperties map.
     */
    @JsonAnySetter
    public void setUnknownProperties(String key, Object value) {
        unknownProperties.put(key, value);
    }
}