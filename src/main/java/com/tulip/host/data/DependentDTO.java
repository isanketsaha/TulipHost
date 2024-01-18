package com.tulip.host.data;

import com.tulip.host.web.rest.vm.FileUploadVM;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
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

    List<FileUploadVM> aadhaarCard;

    private Boolean whatsappAvailable;
}
