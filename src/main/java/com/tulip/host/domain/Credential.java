package com.tulip.host.domain;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
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
@Table(name = "credential")
public class Credential extends AbstractAuditingEntity {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 150)
    @NotNull
    @Column(name = "password", nullable = false, length = 150)
    private String password;

    @NotNull
    @Column(name = "reset_password", nullable = false)
    private Boolean resetPassword = false;

    @Size(max = 20)
    @NotNull
    @Column(name = "user_name", nullable = false, length = 20)
    private String userName;
}
