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
@Table(name = "dependent")
public class Dependent extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 20)
    @Column(name = "contact", length = 20)
    private String contact;

    @Size(max = 50)
    @Column(name = "name", length = 50)
    private String name;

    @Size(max = 20)
    @Column(name = "occupation", length = 20)
    private String occupation;

    @Size(max = 20)
    @Column(name = "qualification", length = 20)
    private String qualification;

    @Size(max = 20)
    @Column(name = "relationship", length = 20)
    private String relationship;

    @Size(max = 15)
    @NotNull
    @Column(name = "aadhaar_no", nullable = false, length = 15)
    private String aadhaarNo;

    @ManyToMany(mappedBy = "dependents", fetch = FetchType.EAGER)
    private Set<Student> students = new LinkedHashSet<>();
}
