package com.tulip.host.data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeBasicDTO {

    private String bloodGroup;

    private Date dob;

    private Long id;

    @Size(max = 50)
    @NotNull
    private String name;

    private int age;

    @Size(max = 20)
    @NotNull
    private String phoneNumber;

    @Size(max = 6)
    private String gender;

    @NotNull
    private Boolean active;

    private String address;

    private String classTeacher;
    private String authority;

    @Size(max = 10)
    private String tid;
}
