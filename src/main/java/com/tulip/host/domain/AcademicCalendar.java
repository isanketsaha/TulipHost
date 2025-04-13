package com.tulip.host.domain;

import com.tulip.host.enums.TypeEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDate;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "academic_calendar")
public class AcademicCalendar extends AbstractAuditingEntity {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Size(max = 255)
    @Column(name = "description")
    private String description;

    @Column(name = "eventType")
    @Enumerated(EnumType.STRING)
    private TypeEnum eventType;

    private LocalDate startDate;

    private LocalDate endDate;

    private String recurringPattern;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer")
    private Employee organizer;
}
