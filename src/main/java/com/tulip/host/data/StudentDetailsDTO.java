package com.tulip.host.data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class StudentDetailsDTO {

    private final Long id;

    @Size(max = 50)
    @NotNull
    private final String name;

    @NotNull
    private final LocalDate dob;

    @NotNull
    private final Instant admissionDate;

    @Size(max = 255)
    private final String address;

    @NotNull
    private final ClassDetailDTO classDetails;

    @Size(max = 2)
    @NotNull
    private final String bloodGroup;

    @Size(max = 6)
    @NotNull
    private final String gender;

    @NotNull
    private final Boolean isActive;

    @Size(max = 50)
    private final String previousSchool;

    private final LocalDate terminationDate;

    @NotNull
    private List<DependentDTO> dependent;
}
