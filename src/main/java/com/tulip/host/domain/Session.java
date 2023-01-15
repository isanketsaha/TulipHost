package com.tulip.host.domain;

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
@Table(name = "session")
public class Session extends AbstractAuditingEntity {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 10)
    @NotNull
    @Column(name = "display_text", nullable = false, length = 10)
    private String displayText;

    @NotNull
    @Column(name = "from_date", nullable = false)
    private Date fromDate;

    @NotNull
    @Column(name = "to_date", nullable = false)
    private Date toDate;
}
