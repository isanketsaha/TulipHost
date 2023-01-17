package com.tulip.host.web.rest.vm;

import com.tulip.host.enums.BloodGroupEnum;
import com.tulip.host.enums.GenderEnum;
import com.tulip.host.enums.ReligionEnum;
import com.tulip.host.enums.StdEnum;
import java.util.Date;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnboardingVM {

    @NotNull
    String type;

    String qualification;

    @NotNull
    String address;

    BloodGroupEnum bloodGroup;

    @NotNull
    String contact;

    @NotNull
    List<DependentVM> dependent;

    @NotNull
    Date dob;

    GenderEnum gender;

    @NotNull
    String name;

    String previousSchool;
    ReligionEnum religion;
    Long session;
    BankVM bank;
    InterviewVM interview;
    StdEnum std;
}
