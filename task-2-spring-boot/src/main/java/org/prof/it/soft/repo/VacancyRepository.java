package org.prof.it.soft.repo;

import org.prof.it.soft.entity.Vacancy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Set;

public interface VacancyRepository extends JpaRepository<Vacancy, Long>, JpaSpecificationExecutor<Vacancy> {

    Set<Vacancy> findAllByRecruiterId(Long recruiterId);

    @EntityGraph(attributePaths = {"recruiter", "technologyStack", "recruiter.person"})
    List<Vacancy> findAll(Specification<Vacancy> spec);
}
