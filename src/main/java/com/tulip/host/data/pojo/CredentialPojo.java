package com.tulip.host.data.pojo;

import java.io.Serializable;
import java.time.Instant;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 * A DTO for the {@link com.tulip.host.domain.Credential} entity
 */
@Data
public class CredentialPojo implements Serializable {

    private final String createdBy;
    private final Instant createdDate;
    private final String lastModifiedBy;
    private final Instant lastModifiedDate;
    private final Long id;

    @Size(max = 150)
    @NotNull
    private final String password;

    @NotNull
    private final Boolean resetPassword;

    @Size(max = 20)
    @NotNull
    private final String userName;
}
