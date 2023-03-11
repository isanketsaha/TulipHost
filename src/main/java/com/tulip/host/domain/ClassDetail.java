package com.tulip.host.domain;

import java.time.Instant;
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
@Table(name = "class_details")
public class ClassDetail extends AbstractAuditingEntity {

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
}
