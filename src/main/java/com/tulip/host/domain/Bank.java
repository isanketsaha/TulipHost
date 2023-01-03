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
@Table(name = "bank")
public class Bank extends AbstractAuditingEntity {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "account_no", nullable = false)
    private Long accountNo;

    @Size(max = 30)
    @NotNull
    @Column(name = "ifsc", nullable = false, length = 30)
    private String ifsc;

    @Size(max = 30)
    @Column(name = "bank_name", length = 30)
    private String bankName;
}
