package com.tulip.host.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
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
@Table(name = "interview")
public class Interview extends AbstractAuditingEntity {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "interview_date", nullable = false)
    private Date interviewDate;

    @NotNull
    @Column(name = "doj", nullable = false)
    private Date doj;

    @Column(name = "salary")
    private Integer salary;

    @Lob
    @Column(name = "comments")
    private String comments;
}
