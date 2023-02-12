package com.tulip.host.data;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A DTO for the {@link com.tulip.host.domain.ParentsDetail} entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DependentDTO implements Serializable {

    private Long id;

    @Size(max = 20)
    private String contact;

    @Size(max = 50)
    private String name;

    @Size(max = 20)
    private String occupation;

    @Size(max = 20)
    private String qualification;

    @Size(max = 20)
    private String relationship;

    @Size(max = 15)
    @NotNull
    private String aadhaarNo;

    private Boolean whatsappAvailable;
}
