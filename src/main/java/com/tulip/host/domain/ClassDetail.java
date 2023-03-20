package com.tulip.host.domain;

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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "class_details")
@EqualsAndHashCode
@FilterDef(name = "filterClass", parameters = @ParamDef(name = "classId", type = "long"))
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

    @OneToMany(mappedBy = "std", fetch = FetchType.LAZY)
    private Set<FeesCatalog> feesCatalogs = new LinkedHashSet<>();

    @ManyToMany(mappedBy = "classDetails", fetch = FetchType.LAZY)
    private Set<Student> students = new LinkedHashSet<>();

    @OneToMany(mappedBy = "std", fetch = FetchType.LAZY)
    private Set<ProductCatalog> productCatalogs = new LinkedHashSet<>();

    @OneToMany(mappedBy = "classDetail", fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    private Set<Upload> uploadedDocuments;

    @Override
    public int compareTo(ClassDetail o) {
        return this.getCreatedDate().compareTo(o.getCreatedDate());
    }
}
