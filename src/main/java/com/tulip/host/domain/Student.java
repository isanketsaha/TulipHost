package com.tulip.host.domain;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SortComparator;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "student")
@FilterDef(name = "activeStudent", defaultCondition = "active = :flag", parameters = @ParamDef(name = "flag", type = Boolean.class))
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
    private LocalDate dob;

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

    private String aadhaar;

    @Column(name = "active")
    @Builder.Default
    private Boolean active = true;

    @Column(name = "whatsapp_available")
    @Builder.Default
    private Boolean whatsappAvailable = false;

    @Column(name = "evening_classes")
    @Builder.Default
    private Boolean eveningClass = false;

    @Size(max = 20)
    @NotNull
    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Size(max = 50)
    @Column(name = "previous_school", length = 50)
    private String previousSchool;

    @Column(name = "termination_date")
    private LocalDateTime terminationDate;

    @Size(max = 20)
    @Column(name = "religion", length = 20)
    private String religion;

    @OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "picture_id")
    private Upload profilePicture;

    @OneToOne
    @JoinColumn(name = "letter_id")
    private Upload enrolLetter;

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    private Set<Upload> uploadedDocuments;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
        name = "student_to_dependent",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "dependent_id")
    )
    private Set<Dependent> dependents = new LinkedHashSet<>();

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    @Filter(name = "filterTransactionOnType")
    @OrderBy("created_date DESC")
    private Set<Transaction> transactions = new LinkedHashSet<>();

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    @OrderBy("created_date DESC")
    private Set<StudentToTransport> transports = new TreeSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE })
    @JoinTable(
        name = "student_to_class",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "class_id")
    )
    @Filter(name = "filterClass")
    @SortComparator(ClassComparatorBySession.class)
    private SortedSet<ClassDetail> classDetails = new TreeSet<>();

    public void addClass(ClassDetail classDetail) {
        if (classDetails == null) {
            SortedSet<ClassDetail> classList = new TreeSet();
            classList.add(classDetail);
            this.setClassDetails(classList);
        } else {
            classDetails.add(classDetail);
        }
    }

    public void addTransport(StudentToTransport studentToTransport) {
        if (transports == null) {
            SortedSet<StudentToTransport> transportList = new TreeSet();
            transportList.add(studentToTransport);
            this.setTransports(transportList);
        } else {
            transports.add(studentToTransport);
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

    public void addUpload(Set<Upload> upload) {
        if (uploadedDocuments == null) {
            Set<Upload> uploadSet = new LinkedHashSet<>();
            uploadSet.addAll(upload);
            this.setUploadedDocuments(uploadSet);
        } else {
            this.uploadedDocuments.addAll(upload);
        }
        upload.forEach(item -> item.setStudent(this));
    }
}
