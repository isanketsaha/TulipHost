package com.tulip.host.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Embeddable
public class StudentToDependentId implements Serializable {

    private static final long serialVersionUID = -2735605714687044212L;

    @NotNull
    @Column(name = "dependent_id", nullable = false)
    private Long dependentId;

    @NotNull
    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        StudentToDependentId entity = (StudentToDependentId) o;
        return Objects.equals(this.studentId, entity.studentId) && Objects.equals(this.dependentId, entity.dependentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, dependentId);
    }
}
