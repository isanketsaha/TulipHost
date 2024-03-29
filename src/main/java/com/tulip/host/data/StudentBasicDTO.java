package com.tulip.host.data;

import com.tulip.host.enums.StdEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
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

    private long pendingFees;

    @NotNull
    private String std;

    private Long classId;
    private String address;
    private int age;
    private List<String> annualPaidFees;
    private LocalDateTime createdDate;
}
