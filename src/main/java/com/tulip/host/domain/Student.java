package com.tulip.host.domain;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
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

    @OneToMany(mappedBy = "dependent", fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private Set<UserToDependent> dependents;

    @NotNull
    @OneToMany(mappedBy = "std", fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private Set<StudentToClass> std;

    public void addStd(StudentToClass studentToClass) {
        studentToClass.setStudent(this);
        if (std == null) {
            Set<StudentToClass> classList = new HashSet();
            classList.add(studentToClass);
            this.setStd(classList);
        } else {
            std.add(studentToClass);
        }
    }

    public void addDependents(UserToDependent userToDependent) {
        userToDependent.setStudent(this);
        if (dependents == null) {
            Set<UserToDependent> dependentSet = new HashSet();
            dependentSet.add(userToDependent);
            this.setDependents(dependentSet);
        } else {
            dependents.add(userToDependent);
        }
    }
}
