package com.tulip.host.domain;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
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
public class Student extends AbstractAuditingEntity<Long> implements Serializable {

    @Id
    @Column(name = "student_id", nullable = false)
    private Long id;

    @Size(max = 50)
    @NotNull
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @NotNull
    @Column(name = "dob", nullable = false)
    private LocalDate dob;

    @Size(max = 255)
    @Column(name = "address")
    private String address;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "class_detailsfk", nullable = false)
    private ClassDetail classDetailsfk;

    @Size(max = 2)
    @NotNull
    @Column(name = "blood_group", nullable = false, length = 2)
    private String bloodGroup;

    @Size(max = 6)
    @NotNull
    @Column(name = "gender", nullable = false, length = 6)
    private String gender;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = false;

    @Size(max = 50)
    @Column(name = "previous_school", length = 50)
    private String previousSchool;

    @Column(name = "termination_date")
    private LocalDate terminationDate;
}
