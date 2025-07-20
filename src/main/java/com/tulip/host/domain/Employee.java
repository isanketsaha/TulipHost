package com.tulip.host.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.tulip.host.utils.ClassComparatorBySession;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SortComparator;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "employee")
public class Employee extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_id", nullable = false)
    private Long id;

    @Size(max = 10)
    @Column(name = "tid", length = 10)
    private String tid;

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
    private LocalDate dob;

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

    private String aadhaar;

    @NotNull
    @Column(name = "locked", nullable = false)
    @Builder.Default
    private Boolean locked = false;

    @OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "picture_id")
    private Upload profilePicture;

    @OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "letter_id")
    private Upload appointmentLetter;

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

    @Column(name = "termination_date")
    private LocalDateTime terminationDate;

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "organizer")
    private List<AcademicCalendar> events;

    public void addDocuments(Set<Upload> uploadSet) {
        if (uploadedDocuments == null) {
            LinkedHashSet<Upload> objects = new LinkedHashSet<>();
            objects.addAll(uploadSet);
            this.setUploadedDocuments(objects);
        } else {
            uploadedDocuments.addAll(uploadSet);
        }
        uploadedDocuments.forEach(item -> item.setEmployee(this));
    }
}
