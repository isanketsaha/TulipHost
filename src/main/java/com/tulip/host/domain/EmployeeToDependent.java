package com.tulip.host.domain;

import javax.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "employee_to_dependent")
public class EmployeeToDependent {

    @EmbeddedId
    private EmployeeToDependentId id;

    @MapsId("dependentId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dependent_id", nullable = false)
    private Dependent dependent;

    @MapsId("empId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee emp;
}
