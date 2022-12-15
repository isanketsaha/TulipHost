package com.tulip.host.data.pojo;

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
public class LoginPojo implements Serializable {

    @NotNull
    private String name;

    @NotNull
    private Boolean active;

    @NotNull
    private String authority;

    @NotNull
    private Boolean resetPassword;

    @Size(max = 20)
    @NotNull
    private String userName;

    private String password;
}
