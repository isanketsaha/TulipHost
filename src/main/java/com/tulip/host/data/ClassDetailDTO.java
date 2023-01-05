package com.tulip.host.data;

import java.io.Serializable;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A DTO for the {@link ClassDetail} entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassDetailDTO implements Serializable {

    private Long id;

    @Size(max = 10)
    private String std;

    private String headTeacher;

    private String session;

    private int studentStrength;
}
