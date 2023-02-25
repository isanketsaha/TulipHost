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
public class UserEditVM {

    @NotNull
    Long id;

    String type;
    String qualification;
    String address;
    BloodGroupEnum bloodGroup;
    String contact;
    boolean whatsappAvailable;
    List<DependentVM> dependent;
    String experience;
    Date dob;
    GenderEnum gender;
    String name;
    String previousSchool;
    ReligionEnum religion;
    Long session;
    StdEnum std;
}
