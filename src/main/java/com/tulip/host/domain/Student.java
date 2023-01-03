package com.tulip.host.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
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
    @Column(name = "std_id", nullable = false)
    private Long std;

    @Size(max = 2)
    @NotNull
    @Column(name = "blood_group", nullable = false, length = 2)
    private String bloodGroup;

    @Size(max = 6)
    @NotNull
    @Column(name = "gender", nullable = false, length = 6)
    private String gender;

    @Column(name = "active", nullable = false)
    private Boolean isActive = Boolean.TRUE;

    @Size(max = 20)
    @NotNull
    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Size(max = 50)
    @Column(name = "previous_school", length = 50)
    private String previousSchool;

    @Column(name = "termination_date")
    private LocalDate terminationDate;

    @Column(name = "religion")
    private String religion;
}
