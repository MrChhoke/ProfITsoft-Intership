package org.prof.it.soft.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents a Person entity.
 * It includes fields for the person's first name and last name.
 *
 * The class is annotated with Lombok annotations to automatically generate getters, setters,
 * a builder, and equals/hashCode methods.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    /**
     * This field represents the person's first name.
     */
    protected String firstName;

    /**
     * This field represents the person's last name.
     */
    protected String lastName;

}