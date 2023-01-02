package com.tulip.host.domain;

import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "employee")
public class Employee extends AbstractAuditingEntity {

    @Id
    @Column(name = "emp_id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Size(max = 255)
    @Column(name = "address")
    private String address;

    @Size(max = 4)
    @Column(name = "blood_group", length = 4)
    private String bloodGroup;

    @Column(name = "dob")
    private Instant dob;

    @Size(max = 255)
    @Column(name = "experience")
    private String experience;

    @Size(max = 6)
    @Column(name = "gender", length = 6)
    private String gender;

    @Column(name = "leave_balance")
    private Double leaveBalance;

    @NotNull
    @Column(name = "locked", nullable = false)
    private Boolean locked = false;

    @Size(max = 50)
    @NotNull
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "credential_id")
    private Long credential;

    @Size(max = 20)
    @NotNull
    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Size(max = 20)
    @Column(name = "qualification", length = 20)
    private String qualification;

    @Size(max = 20)
    @Column(name = "religion", length = 20)
    private String religion;

    @NotNull
    @Column(name = "group_id", length = 20)
    private Long group;

    @NotNull
    @Column(name = "bank_id", length = 20)
    private Long bank;

    @NotNull
    @Column(name = "interview_id", length = 20)
    private Long interview;
}
