package com.tulip.host.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@Data
public class StudentToTransportId implements Serializable {

    private static final long serialVersionUID = -2735605714687044212L;

    @NotNull
    @Column(name = "transport_id", nullable = false)
    private Long transportId;

    @NotNull
    @Column(name = "student_id", nullable = false)
    private Long studentId;
}
