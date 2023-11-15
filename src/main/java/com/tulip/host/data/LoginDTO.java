package com.tulip.host.data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
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
