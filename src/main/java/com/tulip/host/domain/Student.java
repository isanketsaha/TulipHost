package com.tulip.host.domain;

import com.tulip.host.utils.ClassComparatorBySession;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
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
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterJoinTable;
import org.hibernate.annotations.FilterJoinTables;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SortComparator;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "student")
@FilterDef(name = "activeStudent", defaultCondition = "active = :flag", parameters = @ParamDef(name = "flag", type = "boolean"))
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
    private Date terminationDate;

    @Size(max = 20)
    @Column(name = "religion", length = 20)
    private String religion;

    private String profilePicture;

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
