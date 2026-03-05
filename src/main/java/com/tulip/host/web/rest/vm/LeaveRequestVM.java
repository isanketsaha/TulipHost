package com.tulip.host.web.rest.vm;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;

@Data
public class LeaveRequestVM {

    @NotNull
    private LocalDate startDate;

    @NotNull
    @Min(1)
    private int numberOfDays;

    private String remarks;
}
