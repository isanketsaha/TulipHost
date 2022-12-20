package com.tulip.host.web.rest.vm;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginVM {

    @NotNull
    @Size(min = 4, max = 50)
    private String username;

    @NotNull
    @Size(min = 4, max = 20)
    private String password;

    private Boolean rememberMe;
}
