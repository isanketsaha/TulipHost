package com.tulip.host.web.rest.vm;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApplyLeaveVM {

    private Long employeeId;

    private String tid;

    @NotNull
    private Long leaveTypeId;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    private String reason;

    private Boolean isHalfDay = false;
}