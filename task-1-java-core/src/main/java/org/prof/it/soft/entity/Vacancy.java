package org.prof.it.soft.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

/**
 * This class represents a Vacancy entity.
 * It includes fields for the position, salary, technology stack, and recruiter.
 *
 * The class is annotated with Lombok annotations to automatically generate getters, setters,
 * a builder, and equals/hashCode methods.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vacancy {

    /**
     * This field represents the position of the vacancy.
     */
    protected String position;

    /**
     * This field represents the salary of the vacancy.
     */
    protected Float salary;

    /**
     * This field represents the technology stack of the vacancy.
     * It is a list of strings.
     */
    protected List<String> technologyStack;

    /**
     * This field represents the recruiter of the vacancy.
     * It is a Recruiter object.
     */
    protected Recruiter recruiter;

    /**
     * This method overrides the equals method from the Object class.
     * It checks if the given object is equal to this vacancy by comparing their position, salary, technology stack, and recruiter.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vacancy vacancy = (Vacancy) o;
        return Objects.equals(position, vacancy.position) &&
                Objects.equals(salary, vacancy.salary) &&
                Objects.equals(technologyStack, vacancy.technologyStack) &&
                Objects.equals(recruiter.getFirstName(), vacancy.recruiter.getFirstName()) &&
                Objects.equals(recruiter.getLastName(), vacancy.recruiter.getLastName()) &&
                Objects.equals(recruiter.getCompanyName(), vacancy.recruiter.getCompanyName());
    }

    /**
     * This method overrides the hashCode method from the Object class.
     * It returns a hash code value for this vacancy, which is computed based on the position, salary, technology stack, and recruiter.
     */
    @Override
    public int hashCode() {
        return Objects.hash(position, salary, technologyStack,
                recruiter.getFirstName(), recruiter.getLastName(), recruiter.getCompanyName());
    }
}