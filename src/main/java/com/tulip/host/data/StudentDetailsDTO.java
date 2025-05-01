package com.tulip.host.data;

import com.tulip.host.web.rest.vm.FileUploadVM;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;

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

    String aadhaar;

    String tc;

    private String terminationReason;

    @NotNull
    private List<ClassDetailDTO> classDetails;

    @Size(max = 2)
    @NotNull
    private String bloodGroup;

    private String profilePictureUrl;

    @Size(max = 6)
    @NotNull
    private String gender;

    @NotNull
    private Boolean active;

    private Boolean whatsappAvailable;

    boolean eveningClass;

    @Size(max = 50)
    private String previousSchool;

    private Date terminationDate;

    private String religion;
    private int age;
    List<FileUploadVM> aadhaarCard;

    List<FileUploadVM> profilePicture;

    List<FileUploadVM> panCard;

    List<FileUploadVM> birthCertificate;

    @NotNull
    private Set<DependentDTO> dependent;

    private TransportOptDTO transports;
}
