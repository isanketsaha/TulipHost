package com.tulip.host.data;

import com.tulip.host.web.rest.vm.UploadVM;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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

    List<UploadVM> aadhaarCard = new ArrayList<>();

    List<UploadVM> profilePicture = new ArrayList<>();

    List<UploadVM> panCard = new ArrayList<>();

    private String profilePictureUrl;

    private Instant createdDate;
}
