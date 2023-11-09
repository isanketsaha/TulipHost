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
public class StudentToTransportId implements Serializable {

    private static final long serialVersionUID = -2735605714687044212L;

    @NotNull
    @Column(name = "transport_id", nullable = false)
    private Long transportId;

    @NotNull
    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        StudentToTransportId entity = (StudentToTransportId) o;
        return Objects.equals(this.studentId, entity.studentId) && Objects.equals(this.transportId, entity.transportId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, transportId);
    }
}
