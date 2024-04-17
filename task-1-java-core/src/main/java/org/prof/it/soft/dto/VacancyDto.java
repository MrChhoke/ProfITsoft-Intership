package org.prof.it.soft.dto;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.prof.it.soft.databind.deser.StringListDeserializer;
import org.prof.it.soft.databind.ser.StringListSerializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents a DTO (Data Transfer Object) for a vacancy.
 * It extends the AbstractDto class and includes fields for the position, salary, technology stack, and recruiter.
 *
 * The class is annotated with Jackson annotations to control its serialization to JSON.
 * The `@JsonProperty` annotation sets the name and order of the JSON properties.
 * The `@JsonInclude` annotation controls the inclusion of null and empty values in the JSON.
 * The `@JsonSerialize` and `@JsonDeserialize` annotations specify the serializers and deserializers to use for the technology stack.
 * The `@JsonUnwrapped` annotation allows the recruiter's properties to be included at the same level as the other properties.
 * The `@JsonIgnore` annotation prevents the unknownProperties map from being included in the JSON.
 * The `@JsonAnySetter` annotation allows any unknown properties in the JSON to be added to the unknownProperties map.
 *
 * The class is also annotated with Lombok annotations to automatically generate getters, setters,
 * a builder, and equals/hashCode methods.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class VacancyDto extends AbstractDto {

    /**
     * This field represents the position of the vacancy.
     */
    @JsonProperty(value = "position", index = 1, required = true)
    protected String position;

    /**
     * This field represents the salary of the vacancy.
     */
    @JsonProperty(value = "salary", index = 2, required = false)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    protected Float salary;

    /**
     * This field represents the technology stack of the vacancy.
     */
    @JsonProperty(value = "technology_stack", index = 3, required = false)
    @JsonSerialize(using = StringListSerializer.class)
    @JsonDeserialize(using = StringListDeserializer.class)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected List<String> technologyStack;

    /**
     * This field represents the recruiter of the vacancy.
     */
    @JsonUnwrapped(prefix = "recruiter_", enabled = true)
    protected RecruiterDto recruiterDto;

    /**
     * This field represents a map of unknown properties.
     * Any properties in the JSON that are not mapped to other fields in this class will be added to this map.
     */
    @JsonIgnore
    protected Map<String, Object> unknownProperties = new HashMap<>();

    /**
     * This constructor creates a new VacancyDto with the given position, salary, technology stack, and recruiter.
     * It throws an IllegalArgumentException if the position or recruiter's first name is null.
     */
    @JsonCreator
    public VacancyDto(@JsonProperty(value = "position", required = true) String position,
                      @JsonProperty(value = "salary") Float salary,
                      @JsonProperty(value = "technology_stack") List<String> technologyStack,
                      @JsonProperty(value = "recruiter_first_name", required = true) String recruiterFirstName,
                      @JsonProperty(value = "recruiter_last_name") String recruiterLastName,
                      @JsonProperty(value = "recruiter_company_name") String recruiterCompanyName) {
        if(position == null) {
            throw new IllegalArgumentException("Position is null");
        }

        if(recruiterFirstName == null) {
            throw new IllegalArgumentException("Recruiter first name is null");
        }

        this.position = position;
        this.salary = salary;
        this.technologyStack = technologyStack;
        this.recruiterDto = new RecruiterDto(recruiterFirstName, recruiterLastName, recruiterCompanyName);
    }

    /**
     * This method sets the recruiter of the vacancy.
     * If the recruiter is already set, it updates the unknown properties of the existing recruiter.
     */
    public void setRecruiterDto(RecruiterDto recruiterDto) {
        if (this.recruiterDto == null) {
            this.recruiterDto = recruiterDto;
        }

        if(this.recruiterDto != null) {
            this.recruiterDto.setUnknownProperties(recruiterDto.getUnknownProperties());
        }
    }

    /**
     * This method allows any unknown properties in the JSON to be added to the unknownProperties map.
     */
    @JsonAnySetter
    public void setUnknownProperty(String name, Object value) {
        unknownProperties.put(name, value);
    }

    /**
     * This method returns the map of unknown properties, including those of the recruiter.
     */
    public Map<String, Object> getUnknownProperties() {
        unknownProperties.putAll(recruiterDto.unknownProperties);
        return unknownProperties;
    }
}