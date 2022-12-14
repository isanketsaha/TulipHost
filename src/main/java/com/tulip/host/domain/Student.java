package com.tulip.host.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
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
@Table(name = "student")
public class Student extends AbstractAuditingEntity {

    @Id
    @Column(name = "student_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 50)
    @NotNull
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @NotNull
    @Column(name = "dob", nullable = false)
    private Date dob;

    @Size(max = 255)
    @Column(name = "address")
    private String address;

    @NotNull
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "std_id", nullable = false)
    private ClassDetail std;

    @Size(max = 2)
    @NotNull
    @Column(name = "blood_group", nullable = false, length = 2)
    private String bloodGroup;

    @Size(max = 6)
    @NotNull
    @Column(name = "gender", nullable = false, length = 6)
    private String gender;

    @Column(name = "active")
    @Builder.Default
    private Boolean active = true;

    @Size(max = 20)
    @NotNull
    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Size(max = 50)
    @Column(name = "previous_school", length = 50)
    private String previousSchool;

    @Column(name = "termination_date")
    private LocalDate terminationDate;

    @Size(max = 20)
    @Column(name = "religion", length = 20)
    private String religion;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "student")
    private List<Dependent> dependent;
}
