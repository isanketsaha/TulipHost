package com.tulip.host.data;

import com.tulip.host.domain.UserGroup;
import java.io.Serializable;
import java.time.Instant;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
