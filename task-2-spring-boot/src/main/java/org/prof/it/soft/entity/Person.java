package org.prof.it.soft.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "people")
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    /**
     * The primary key of the Person entity.
     */
    @Id
    @EqualsAndHashCode.Include
    @SequenceGenerator(name = "person_id_seq", sequenceName = "people_seq_id", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "person_id_seq")
    @Column(name = "id", columnDefinition = "bigint", nullable = false)
    protected Long id;

    /**
     * The first name of the person.
     */
    @Column(name = "first_name", columnDefinition = "varchar", nullable = false, length = 255)
    protected String firstName;

    /**
     * The last name of the person.
     */
    @Column(name = "last_name", columnDefinition = "varchar", nullable = true, length = 255)
    protected String lastName;

    /**
     * The timestamp when the person entity was created.
     */
    @Column(name = "created_at", columnDefinition = "timestamp", nullable = false)
    protected LocalDateTime createdAt;

    /**
     * The timestamp when the person entity was last updated.
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
        Person person = (Person) o;
        return getId() != null && Objects.equals(getId(), person.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}