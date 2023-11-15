package com.tulip.host.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "class_details")
@FilterDef(name = "filterClass", defaultCondition = "id = :classId", parameters = @ParamDef(name = "classId", type = Long.class))
public class ClassDetail extends AbstractAuditingEntity implements Comparable<ClassDetail> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 10)
    @Column(name = "std", length = 10)
    private String std;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "head_teacher_id")
    private Employee headTeacher;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @OneToMany(mappedBy = "std", fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE })
    private Set<FeesCatalog> feesCatalogs = new LinkedHashSet<>();

    @Filter(name = "activeStudent")
    @ManyToMany(mappedBy = "classDetails", fetch = FetchType.LAZY)
    private Set<Student> students = new LinkedHashSet<>();

    @Filter(name = "filterCatalogNEPlaceholder")
    @OneToMany(mappedBy = "std", fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE })
    private Set<ProductCatalog> productCatalogs = new LinkedHashSet<>();

    @OneToMany(mappedBy = "classDetail", fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE })
    private Set<Upload> uploadedDocuments;

    @Override
    public int compareTo(ClassDetail o) {
        return this.getCreatedDate().compareTo(o.getCreatedDate());
    }
}
