package com.tulip.host.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.time.Instant;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "student_to_transport")
public class StudentToTransport extends AbstractAuditingEntity {

    @EmbeddedId
    private StudentToTransportId id = new StudentToTransportId();

    @MapsId("transportId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transport_id", nullable = false)
    private TransportCatalog transport;

    @MapsId("studentId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(columnDefinition = "DATE")
    private Instant startDate;

    @Column(columnDefinition = "DATE")
    private Instant endDate;
}
