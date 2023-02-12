package com.tulip.host.data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class StudentDetailsDTO {

    private Long id;

    @Size(max = 50)
    @NotNull
    private String name;

    @NotNull
    private Date dob;

    @NotNull
    private Date admissionDate;

    @Size(max = 255)
    private String address;

    @NotNull
    private String phoneNumber;

    @NotNull
    private List<ClassDetailDTO> classDetails;

    @Size(max = 2)
    @NotNull
    private String bloodGroup;

    @Size(max = 6)
    @NotNull
    private String gender;

    @NotNull
    private Boolean active;

    private Boolean whatsappAvailable;

    @Size(max = 50)
    private String previousSchool;

    private Date terminationDate;

    private String religion;
    private int age;

    @NotNull
    private Set<DependentDTO> dependent;
}
