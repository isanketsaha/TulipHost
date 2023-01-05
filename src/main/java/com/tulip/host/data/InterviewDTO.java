package com.tulip.host.data;

import com.tulip.host.enums.UserRoleEnum;
import java.util.Date;
import javax.validation.constraints.NotNull;
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
