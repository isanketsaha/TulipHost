package com.tulip.host.data;

import com.tulip.host.domain.Employee;
import java.io.Serializable;
import java.time.Instant;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A DTO for the {@link Employee} entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDetailsDTO implements Serializable {

    private Long id;

    @NotNull
    private Boolean active;

    @Size(max = 255)
    private String address;

    @Size(max = 4)
    private String bloodGroup;

    private Instant dob;

    @Size(max = 255)
    private String experience;

    @Size(max = 50)
    private String father;

    @Size(max = 6)
    private String gender;

    private Double leaveBalance;

    @NotNull
    private Boolean locked;

    @Size(max = 50)
    @NotNull
    private String name;

    @Size(max = 20)
    @NotNull
    private String phoneNumber;

    @Size(max = 20)
    private String qualification;

    @Size(max = 20)
    private String religion;

    @NotNull
    private String authority;
}
