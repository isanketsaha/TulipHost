package com.tulip.host.domain;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.*;
import org.hibernate.Hibernate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Embeddable
public class StudentToClassId implements Serializable {

    private static final long serialVersionUID = 1461524342187912676L;

    @NotNull
    @Column(name = "class_id", nullable = false)
    private Long classId;

    @NotNull
    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        StudentToClassId entity = (StudentToClassId) o;
        return Objects.equals(this.studentId, entity.studentId) && Objects.equals(this.classId, entity.classId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, classId);
    }
}
