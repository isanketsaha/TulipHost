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
@Table(name = "fees")
public class Fee extends AbstractAuditingEntity {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "amount", nullable = false)
    private Double amount;

    @Size(max = 10)
    @Column(name = "from_month", length = 10)
    private String fromMonth;

    @Size(max = 10)
    @Column(name = "to_month", length = 10)
    private String toMonth;

    @Column(name = "payment_mode_id")
    private Long paymentMode;
}
