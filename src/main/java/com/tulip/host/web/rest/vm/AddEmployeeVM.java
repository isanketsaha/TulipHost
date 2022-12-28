package com.tulip.host.web.rest.vm;

import com.tulip.host.enums.BloodGroup;
import com.tulip.host.enums.Gender;
import java.time.Instant;
import java.util.Date;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class AddEmployeeVM {

    @Size(max = 255)
    @Column(name = "address")
    private String address;

    @Size(max = 4)
    private BloodGroup bloodGroup;

    private Date dob;

    @Size(max = 255)
    private String experience;

    @Size(max = 50)
    private String father;

    @Size(max = 6)
    private Gender gender;

    @Size(max = 50)
    @NotNull
    private String name;

    @Size(max = 20)
    @NotNull
    private String phoneNumber;

    @Size(max = 20)
    private String highestQualification;

    @Size(max = 20)
    private String religion;

    @NotNull
    private String employeeType;
}
