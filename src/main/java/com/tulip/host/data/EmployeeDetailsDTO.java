package com.tulip.host.data;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
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

    @Size(max = 6)
    private String gender;

    @NotNull
    private Boolean locked;

    @NotNull
    private String name;

    private Boolean whatsappAvailable;

    @NotNull
    private String phoneNumber;

    private String qualification;

    private String religion;

    @NotNull
    private String authority;

    @NotNull
    private List<DependentDTO> dependent;

    private BankDTO bank;

    private InterviewDTO interview;
}
