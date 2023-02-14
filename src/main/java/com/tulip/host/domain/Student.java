package com.tulip.host.domain;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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

    @Size(max = 6)
    @Column(name = "blood_group", nullable = false, length = 6)
    private String bloodGroup;

    @NotNull
    @Lob
    @Column(name = "gender", nullable = false)
    private String gender;

    @Column(name = "active")
    @Builder.Default
    private Boolean active = true;

    @Column(name = "whatsapp_available")
    @Builder.Default
    private Boolean whatsappAvailable = false;

    @Size(max = 20)
    @NotNull
    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Size(max = 50)
    @Column(name = "previous_school", length = 50)
    private String previousSchool;

    @Column(name = "termination_date")
    private Date terminationDate;

    @Size(max = 20)
    @Column(name = "religion", length = 20)
    private String religion;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
        name = "student_to_dependent",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "dependent_id")
    )
    private Set<Dependent> dependents = new LinkedHashSet<>();

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private Set<Transaction> transactions = new LinkedHashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE })
    @JoinTable(
        name = "student_to_class",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "class_id")
    )
    private Set<ClassDetail> classDetails = new LinkedHashSet<>();

    public void addClass(ClassDetail classDetail) {
        if (classDetails == null) {
            Set<ClassDetail> classList = new LinkedHashSet();
            classList.add(classDetail);
            this.setClassDetails(classList);
        } else {
            classDetails.add(classDetail);
        }
    }

    public void addDependent(Dependent dependent) {
        if (dependents == null) {
            Set<Dependent> dependents = new LinkedHashSet<>();
            dependents.add(dependent);
            this.setDependents(dependents);
        } else {
            dependents.add(dependent);
        }
    }
}
