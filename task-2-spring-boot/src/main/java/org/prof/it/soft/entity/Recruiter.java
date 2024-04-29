package org.prof.it.soft.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "recruiters")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recruiter {

    /**
     * The primary key of the Recruiter entity.
     */
    @Id
    @EqualsAndHashCode.Include
    @SequenceGenerator(name = "recruiter_id_seq", sequenceName = "recruiters_seq_id", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "recruiter_id_seq")
    @Column(name = "id", columnDefinition = "bigint", nullable = false)
    protected Long id;

    /**
     * The name of the company the recruiter works for.
     */
    @Column(name = "company_name", columnDefinition = "varchar", nullable = true, length = 255)
    protected String companyName;

    /**
     * The vacancies associated with the recruiter.
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "recruiter")
    @Builder.Default
    @ToString.Exclude
    protected Set<Vacancy> vacancies = new HashSet<>();

    /**
     * The person associated with the recruiter.
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "person_id")
    @ToString.Exclude
    protected Person person;

    /**
     * The timestamp when the recruiter entity was created.
     */
    @Column(name = "created_at", columnDefinition = "timestamp", nullable = false)
    protected LocalDateTime createdAt;

    /**
     * The timestamp when the recruiter entity was last updated.
     */
    @Column(name = "updated_at", columnDefinition = "timestamp", nullable = false)
    protected LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * This method adds a vacancy to the recruiter's vacancies.
     * It also sets the recruiter of the vacancy to this recruiter.
     */
    public void addVacancy(Vacancy vacancy) {
        vacancies.add(vacancy);
        vacancy.setRecruiter(this);
    }

    /**
     * This method removes a vacancy from the recruiter's vacancies.
     * It also sets the recruiter of the vacancy to null.
     */
    public void removeVacancy(Vacancy vacancy) {
        vacancies.remove(vacancy);
        vacancy.setRecruiter(null);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy
                ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Recruiter recruiter = (Recruiter) o;
        return getId() != null && Objects.equals(getId(), recruiter.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}