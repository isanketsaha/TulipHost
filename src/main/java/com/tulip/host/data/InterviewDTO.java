package com.tulip.host.data;

import com.tulip.host.enums.UserRoleEnum;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewDTO {

    private Long id;

    @NotNull
    private Date doj;

    private Date interviewDate;

    @NotNull
    private Integer salary;

    private String comments;
}
