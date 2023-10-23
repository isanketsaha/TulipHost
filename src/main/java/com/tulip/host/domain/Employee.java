package com.tulip.host.domain;

import com.tulip.host.utils.ClassComparatorBySession;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.SortComparator;

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

    @Column(name = "whatsapp_available")
    @Builder.Default
    private Boolean whatsappAvailable = false;

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

    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
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

    //    @Column(name = "termination_date")
    //    private Date terminationDate;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE }, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private UserGroup group;

    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    @JoinColumn(name = "bank_id")
    private Bank bank;

    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    @JoinColumn(name = "interview_id")
    private Interview interview;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    @JoinTable(
        name = "employee_to_dependent",
        joinColumns = @JoinColumn(name = "emp_id"),
        inverseJoinColumns = @JoinColumn(name = "dependent_id")
    )
    private Set<Dependent> dependents = new LinkedHashSet<>();

    @OneToMany(mappedBy = "headTeacher", fetch = FetchType.LAZY, cascade = { CascadeType.MERGE })
    @SortComparator(ClassComparatorBySession.class)
    private Set<ClassDetail> classDetails = new TreeSet<>();

    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    private Set<Upload> uploadedDocuments;
}
