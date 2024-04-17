package org.prof.it.soft.dto.stats;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.*;
import org.prof.it.soft.databind.ser.MapSerializer;
import org.prof.it.soft.dto.AbstractDto;

import java.util.Map;
import java.util.Objects;

/**
 * This class represents a DTO (Data Transfer Object) for salary vacancy statistics.
 * It extends the AbstractDto class and includes fields for minimum, average, and maximum salary,
 * as well as a map of salary ranges to vacancy counts.
 * <p>
 * The class is annotated with Jackson annotations to control its serialization to XML and JSON.
 * The `@JacksonXmlRootElement` annotation sets the name of the root XML element.
 * The `@JacksonXmlProperty` annotation sets the name of the XML elements for the fields.
 * The `@JsonSerialize` annotation specifies the serializer to use for the map.
 * <p>
 * The class is also annotated with Lombok annotations to automatically generate getters, setters,
 * a builder, and equals/hashCode methods.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JacksonXmlRootElement(localName = "statistic")
public class SalaryVacancyStatsDto extends AbstractDto {

    /**
     * This field represents the minimum salary among the vacancies.
     */
    @JacksonXmlProperty(localName = "min-salary", isAttribute = false)
    protected Double minSalary;

    /**
     * This field represents the average salary among the vacancies.
     */
    @JacksonXmlProperty(localName = "average-salary", isAttribute = false)
    protected Double averageSalary;

    /**
     * This field represents the maximum salary among the vacancies.
     */
    @JacksonXmlProperty(localName = "max-salary", isAttribute = false)
    protected Double maxSalary;

    /**
     * This field represents a map of salary ranges to vacancy counts.
     * The key is the salary range and the value is the vacancy count.
     */
    @JacksonXmlProperty(localName = "vacancy-count-by-salary-statistic")
    @JsonSerialize(using = MapSerializer.class)
    protected Map<Float, Long> vacancyCountBySalary;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SalaryVacancyStatsDto that = (SalaryVacancyStatsDto) o;
        return Math.abs(minSalary - that.minSalary) < 0.0000001 &&
                Math.abs(averageSalary - that.averageSalary) < 0.0000001 &&
                Math.abs(maxSalary - that.maxSalary) < 0.0000001 &&
                Objects.equals(vacancyCountBySalary, that.vacancyCountBySalary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(minSalary, averageSalary, maxSalary, vacancyCountBySalary);
    }
}