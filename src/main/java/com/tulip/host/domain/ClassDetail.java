package com.tulip.host.domain;

import java.time.Instant;
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
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 10)
    @Column(name = "std", length = 10)
    private String std;

    @Column(name = "head_teacher_id", nullable = false)
    private Long headTeacher;

    @NotNull
    @Column(name = "session_id", nullable = false)
    private Long session;
}
