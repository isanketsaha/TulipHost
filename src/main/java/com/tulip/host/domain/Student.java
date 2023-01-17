package com.tulip.host.domain;

import java.time.Instant;
import java.time.LocalDate;
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
@Table(name = "student")
public class Student extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id", nullable = false)
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

    @Size(max = 2)
    @NotNull
    @Column(name = "blood_group", nullable = false, length = 2)
    private String bloodGroup;

    @NotNull
    @Lob
    @Column(name = "gender", nullable = false)
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

    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST })
    @JoinTable(
        name = "student_to_dependent",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "dependent_id")
    )
    private Set<Dependent> dependents = new LinkedHashSet<>();

    @OneToMany(mappedBy = "student", fetch = FetchType.EAGER)
    private Set<Transaction> transactions = new LinkedHashSet<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE })
    @JoinTable(
        name = "student_to_class",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "class_id")
    )
    private Set<ClassDetail> classDetails = new LinkedHashSet<>();

    public void addClass(ClassDetail classDetail) {
        classDetail.getStudents().add(this);
        if (classDetails == null) {
            Set<ClassDetail> classList = new LinkedHashSet();
            classList.add(classDetail);
            this.setClassDetails(classList);
        } else {
            classDetails.add(classDetail);
        }
    }
}
