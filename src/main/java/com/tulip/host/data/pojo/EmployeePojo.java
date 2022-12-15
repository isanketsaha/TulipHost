package com.tulip.host.data.pojo;

import com.tulip.host.domain.Employee;
import java.io.Serializable;
import java.time.Instant;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 * A DTO for the {@link Employee} entity
 */
@Data
public class EmployeePojo implements Serializable {

    private final Long id;

    @NotNull
    private final Boolean active;

    @Size(max = 255)
    private final String address;

    @Size(max = 4)
    private final String bloodGroup;

    private final Instant dob;

    @Size(max = 255)
    private final String experience;

    @Size(max = 50)
    private final String father;

    @Size(max = 6)
    private final String gender;

    private final Double leaveBalance;

    @NotNull
    private final Boolean locked;

    @Size(max = 50)
    @NotNull
    private final String name;

    @Size(max = 20)
    @NotNull
    private final String phoneNumber;

    @Size(max = 20)
    private final String qualification;

    @Size(max = 20)
    private final String religion;

    @NotNull
    private final UserGroupPojo group;
}
