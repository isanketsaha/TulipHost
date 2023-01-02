package com.tulip.host.web.rest.vm;

import com.tulip.host.enums.UserRoleEnum;
import java.util.Date;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewVM {

    @NotNull
    Date doj;

    Date interviewDate;

    @NotNull
    UserRoleEnum role;

    @NotNull
    Integer salary;

    String comments;
}
