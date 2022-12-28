package com.tulip.host.data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class StudentBasicDTO {

    private final Long id;

    @Size(max = 50)
    @NotNull
    private final String name;

    @Size(max = 6)
    @NotNull
    private final String gender;

    @NotNull
    private final Boolean isActive;

    @NotNull
    private ParentsDetailDto parentsDetail;

    @NotNull
    private final ClassDetailDTO classDetails;
}
