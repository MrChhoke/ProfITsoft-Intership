package org.prof.it.soft.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * This class represents a Recruiter entity.
 * It extends the Person class and includes fields for the recruiter's company name and vacancies.
 *
 * The class is annotated with Lombok annotations to automatically generate getters, setters,
 * equals/hashCode methods, and a constructor that includes all fields.
 *
 * The `@EqualsAndHashCode(callSuper = true)` annotation includes the fields of the superclass in the equals and hashCode methods.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Recruiter extends Person {

    /**
     * This field represents the recruiter's company name.
     */
    protected String companyName;

    /**
     * This field represents the vacancies that the recruiter is responsible for.
     * It is a set of Vacancy objects.
     */
    protected Set<Vacancy> vacancies;

    /**
     * This constructor creates a new Recruiter with the given first name, last name, company name, and vacancies.
     * It calls the superclass constructor to set the first name and last name.
     */
    public Recruiter(String firstName,
                     String lastName,
                     String companyName,
                     Set<Vacancy> vacancies) {
        super(firstName, lastName);
        this.companyName = companyName;
        this.vacancies = vacancies;
    }

    /**
     * This method adds a new vacancy to the recruiter's set of vacancies.
     */
    public void addVacancy(Vacancy vacancy) {
        vacancies.add(vacancy);
    }
}