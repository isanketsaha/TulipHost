package com.tulip.host.data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import lombok.Data;

/**
 * A DTO for the {@link UserGroup} entity
 */
@Data
public class UserGroupDTO implements Serializable {

    private final String createdBy;
    private final Instant createdDate;
    private final String lastModifiedBy;
    private final Instant lastModifiedDate;
    private final Long id;

    @Size(max = 10)
    @NotNull
    private final String authority;
}
