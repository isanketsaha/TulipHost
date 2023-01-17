package com.tulip.host.domain;

import java.time.Instant;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Size(max = 255)
    @Column(name = "address")
    private String address;

    @Size(max = 4)
    @Column(name = "blood_group", length = 4)
    private String bloodGroup;

    @Column(name = "dob")
    private Date dob;

    @Size(max = 255)
    @Column(name = "experience")
    private String experience;

    @NotNull
    @Lob
    @Column(name = "gender", nullable = false)
    private String gender;

    @Column(name = "leave_balance")
    private Double leaveBalance;

    @NotNull
    @Column(name = "locked", nullable = false)
    @Builder.Default
    private Boolean locked = false;

    @Size(max = 50)
    @NotNull
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @NotNull
    @Column(name = "reset_credential", nullable = false)
    @Builder.Default
    private Boolean resetCredential = false;

    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST })
    @JoinColumn(name = "credential_id")
    private Credential credential;

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
    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE }, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private UserGroup group;

    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    @JoinColumn(name = "bank_id")
    private Bank bank;

    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    @JoinColumn(name = "interview_id")
    private Interview interview;

    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    @JoinTable(
        name = "employee_to_dependent",
        joinColumns = @JoinColumn(name = "emp_id"),
        inverseJoinColumns = @JoinColumn(name = "dependent_id")
    )
    private Set<Dependent> dependents = new LinkedHashSet<>();

    @OneToMany(mappedBy = "headTeacher", fetch = FetchType.EAGER, cascade = { CascadeType.MERGE })
    private Set<ClassDetail> classDetails = new LinkedHashSet<>();
}
