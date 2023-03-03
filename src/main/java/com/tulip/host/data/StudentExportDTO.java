package com.tulip.host.data;

import java.util.Date;
import javax.validation.constraints.NotNull;
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
