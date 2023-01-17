package com.tulip.host.domain;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.Hibernate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Embeddable
public class EmployeeToDependentId implements Serializable {

    private static final long serialVersionUID = -2785035627671809531L;

    @NotNull
    @Column(name = "dependent_id", nullable = false)
    private Long dependentId;

    @NotNull
    @Column(name = "emp_id", nullable = false)
    private Long empId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        EmployeeToDependentId entity = (EmployeeToDependentId) o;
        return Objects.equals(this.dependentId, entity.dependentId) && Objects.equals(this.empId, entity.empId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dependentId, empId);
    }
}
