package com.tulip.host.web.rest.vm;

import com.tulip.host.enums.UserRoleEnum;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
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

    String userId;
}
