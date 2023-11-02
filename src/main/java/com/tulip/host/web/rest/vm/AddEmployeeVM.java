package com.tulip.host.web.rest.vm;

import com.tulip.host.enums.BloodGroupEnum;
import com.tulip.host.enums.GenderEnum;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Date;
import lombok.Data;

@Data
public class AddEmployeeVM {

    @Size(max = 255)
    @Column(name = "address")
    private String address;

    @Size(max = 4)
    private BloodGroupEnum bloodGroup;

    private Date dob;

    @Size(max = 255)
    private String experience;

    @Size(max = 50)
    private String father;

    @Size(max = 6)
    private GenderEnum gender;

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
