package org.prof.it.soft.dto.stats;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.*;
import org.prof.it.soft.databind.ser.MapSerializer;
import org.prof.it.soft.dto.AbstractDto;
import org.prof.it.soft.dto.RecruiterDto;

import java.util.Map;

/**
 * This class represents a DTO (Data Transfer Object) for recruiter vacancy statistics.
 * It extends the AbstractDto class and includes a map of recruiter DTOs to vacancy counts.
 *
 * The class is annotated with Jackson annotations to control its serialization to XML and JSON.
 * The `@JacksonXmlRootElement` annotation sets the name of the root XML element.
 * The `@JacksonXmlProperty` annotation sets the name of the XML element for the map.
 * The `@JsonSerialize` annotation specifies the serializer to use for the map.
 *
 * The class is also annotated with Lombok annotations to automatically generate getters, setters,
 * a builder, and equals/hashCode methods.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JacksonXmlRootElement(localName = "statistic")
public class RecruiterVacancyStatsDto extends AbstractDto {

    /**
     * This field represents a map of recruiter DTOs to vacancy counts.
     * The key is the recruiter DTO and the value is the vacancy count.
     */
    @JacksonXmlProperty(localName = "vacancy-count-by-recruiter-statistic")
    @JsonSerialize(using = MapSerializer.class)
    protected Map<RecruiterDto, Long> vacancyCountByRecruiter;

}