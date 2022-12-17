package com.tulip.host.data.pojo;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class StudentPojo {

    private final String createdBy;
    private final Instant createdDate;
    private final String lastModifiedBy;
    private final Instant lastModifiedDate;
    private final Long id;

    @Size(max = 50)
    @NotNull
    private final String name;

    @NotNull
    private final LocalDate dob;

    @Size(max = 255)
    private final String address;

    @NotNull
    private final ClassDetailPojo classDetails;

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
}
