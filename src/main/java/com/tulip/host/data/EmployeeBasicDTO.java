package com.tulip.host.data;

import java.time.Instant;
import java.util.Date;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
}
