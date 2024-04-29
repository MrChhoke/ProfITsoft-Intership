package org.prof.it.soft.spec;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.prof.it.soft.dto.filter.VacancyFilterDto;
import org.prof.it.soft.entity.Vacancy;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Specification for filtering vacancies.
 * It supports filtering by position, salary, recruiter,
 * company name, technology stack and creation date.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class VacancySpecification implements Specification<Vacancy> {

    private final String position;
    private final Float minSalary;
    private final Float maxSalary;
    private final Long recruiterId;
    private final String companyName;
    private final List<String> technologyStack;
    private final LocalDateTime createdAtMin;
    private final LocalDateTime createdAtMax;

    /**
     * Creates a new instance of the {@link VacancySpecification} class.
     *
     * @param vacancyFilterDto the DTO with the filter parameters
     * @return the created instance of the {@link VacancySpecification} class
     */
    public static VacancySpecification of(VacancyFilterDto vacancyFilterDto) {
        if (vacancyFilterDto == null) {
            return new VacancySpecification(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }

        return new VacancySpecification(
                vacancyFilterDto.getPosition(),
                vacancyFilterDto.getMinSalary(),
                vacancyFilterDto.getMaxSalary(),
                vacancyFilterDto.getRecruiterId(),
                vacancyFilterDto.getCompanyName(),
                vacancyFilterDto.getTechnologyStack(),
                vacancyFilterDto.getCreatedAtMin(),
                vacancyFilterDto.getCreatedAtMax()
        );
    }

    @Override
    public Predicate toPredicate(Root<Vacancy> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Predicate predicate = criteriaBuilder.conjunction();

        if (position != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("position"), position));
        }

        if (minSalary != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.ge(root.get("salary"), minSalary));
        }

        if (maxSalary != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.le(root.get("salary"), maxSalary));
        }

        if (recruiterId != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("recruiter").get("id"), recruiterId));
        }

        if (companyName != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("recruiter").get("companyName"), companyName));
        }

        if (technologyStack != null && !technologyStack.isEmpty()) {
            Predicate technologyStackPredicate = criteriaBuilder.conjunction();

            for (String technology : technologyStack) {
                technologyStackPredicate = criteriaBuilder.and(technologyStackPredicate, criteriaBuilder.isMember(technology, root.get("technologyStack")));
            }

            predicate = criteriaBuilder.and(predicate, technologyStackPredicate);
        }

        if (createdAtMin != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), createdAtMin));
        }

        if (createdAtMax != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), createdAtMax));
        }

        return predicate;
    }


}
