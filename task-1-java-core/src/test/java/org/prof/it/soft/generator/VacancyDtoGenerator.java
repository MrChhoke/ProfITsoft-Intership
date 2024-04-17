package org.prof.it.soft.generator;

import com.github.javafaker.Faker;
import org.prof.it.soft.dto.RecruiterDto;
import org.prof.it.soft.dto.VacancyDto;

import java.util.*;
import java.util.stream.IntStream;

/**
 * This class is responsible for generating VacancyDto objects for testing purposes.
 * It uses the Faker library to generate random data.
 */
public final class VacancyDtoGenerator {

    public final Faker faker = new Faker(new Locale("en-US"));

    /**
     * Static method to generate a single VacancyDto object.
     * @return a VacancyDto object with random data
     */
    public static VacancyDto generate() {
        return new VacancyDtoGenerator().generateVacancyDto();
    }

    /**
     * Generates a single VacancyDto object with random data.
     * @return a VacancyDto object with random data
     */
    public VacancyDto generateVacancyDto() {
        String position = faker.job().position();
        Float salary = (float) faker.number().randomDouble(0, 500, 10000);

        List<String> skills = List.of("REST", "HTTP", "Agile", "Scrum", "English", "KeyClock", "Spring",
                "Java", "Kotlin", "JavaScript", "TypeScript", "React", "Angular", "Vue", "Node.js", "Express",
                "MongoDB", "PostgreSQL", "MySQL", "Docker", "Kubernetes", "AWS", "Azure", "GCP", "CI/CD", "Jenkins",
                "Git", "Jira", "Confluence", "Trello", "Slack", "Linux", "Windows", "MacOS", "Android", "iOS");
        Set<String> technologyStack = new HashSet<>(skills.subList(0, faker.number().numberBetween(1, skills.size())));

        RecruiterDto recruiterDto = RecruiterDto.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .companyName(faker.company().name())
                .build();

        return VacancyDto.builder()
                .position(position)
                .salary(salary)
                .technologyStack(new ArrayList<>(technologyStack))
                .recruiterDto(recruiterDto)
                .build();
    }

    /**
     * Generates a list of VacancyDto objects with random data.
     * @param size the number of VacancyDto objects to generate
     * @return a list of VacancyDto objects with random data
     */
    public List<VacancyDto> generateVacancyDtos(int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> generateVacancyDto())
                .toList();
    }

}