package org.prof.it.soft.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "vacancies")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vacancy {

    /**
     * The primary key of the Vacancy entity.
     */
    @Id
    @EqualsAndHashCode.Include
    @SequenceGenerator(name = "vacancy_id_seq", sequenceName = "vacancies_seq_id", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vacancy_id_seq")
    @Column(name = "id", columnDefinition = "bigint", nullable = false)
    private Long id;

    /**
     * The position for the vacancy.
     */
    @Column(name = "position", columnDefinition = "varchar", nullable = false, length = 255)
    protected String position;

    /**
     * The salary for the vacancy.
     */
    @Column(name = "salary", columnDefinition = "numeric(10, 2)", nullable = true)
    protected Float salary;

    /**
     * The technology stack required for the vacancy.
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @Column(name = "name")
    @CollectionTable(name = "technology_stacks",
            joinColumns = @JoinColumn(name = "vacancy_id"))
    @Builder.Default
    protected List<String> technologyStack = new ArrayList<>(10);

    /**
     * The recruiter associated with the vacancy.
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false, targetEntity = Recruiter.class)
    @JoinColumn(name = "recruiter_id")
    @ToString.Exclude
    protected Recruiter recruiter;

    /**
     * The timestamp when the vacancy entity was created.
     */
    @Column(name = "created_at", columnDefinition = "timestamp", nullable = false)
    protected LocalDateTime createdAt;

    /**
     * The timestamp when the vacancy entity was last updated.
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
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }

        this.updatedAt = LocalDateTime.now();
    }

    /**
     * This method adds a technology to the technology stack of the vacancy.
     */
    public void addTechnologyStack(String technologyStack) {
        this.technologyStack.add(technologyStack);
    }


    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy
                ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Vacancy vacancy = (Vacancy) o;
        return getId() != null && Objects.equals(getId(), vacancy.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}