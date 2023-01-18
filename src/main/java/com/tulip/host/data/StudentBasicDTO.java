package com.tulip.host.data;

import com.tulip.host.enums.StdEnum;
import java.util.Date;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentBasicDTO {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String gender;

    private Date dob;

    @NotNull
    private Boolean isActive;

    @NotNull
    private String phoneNumber;

    @NotNull
    private String std;

    private Long classId;
    private String address;
    private int age;
}
