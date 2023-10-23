package com.tulip.host.data;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A DTO for the {@link com.tulip.host.domain.Credential} entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO implements Serializable {

    @NotNull
    private String name;

    @NotNull
    private Boolean active;

    @NotNull
    private String authority;

    @NotNull
    private Boolean resetCredential;

    @NotNull
    private Boolean locked;

    @Size(max = 20)
    @NotNull
    private String userId;

    private String password;
}
