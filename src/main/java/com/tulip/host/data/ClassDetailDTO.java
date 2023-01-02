package com.tulip.host.data;

import java.io.Serializable;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 * A DTO for the {@link ClassDetail} entity
 */
@Data
public class ClassDetailDTO implements Serializable {

    private final Long id;

    @Size(max = 10)
    private final String std;

    private final EmployeeDetailsDTO HeadTeacher;
}
