package com.tulip.host.web.rest.vm;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ContactRequestVM {

    @NotBlank
    @Size(max = 100)
    private String name;

    @Email
    private String email;

    @NotBlank
    @Size(max = 12)
    private String phone;

    @NotBlank
    @Size(max = 1000)
    private String message;

    @NotBlank
    @Size(max = 15)
    private String std;

    @NotBlank
    @Size(max = 100)
    private String childName;
}
