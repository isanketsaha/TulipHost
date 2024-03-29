package com.tulip.host.data;

import jakarta.validation.constraints.NotNull;
import java.util.Date;
import lombok.Data;

@Data
public class StudentExportDTO {

    private Long studentId;

    @NotNull
    private String name;

    @NotNull
    private String gender;

    private Date birthday;

    @NotNull
    private String phoneNumber;

    private long pendingFees;

    @NotNull
    private String std;

    private String address;
}
