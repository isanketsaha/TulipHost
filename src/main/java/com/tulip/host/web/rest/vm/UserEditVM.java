package com.tulip.host.web.rest.vm;

import static com.tulip.host.config.Constants.AADHAAR_CARD;
import static com.tulip.host.config.Constants.BIRTH_CERTIFICATE;
import static com.tulip.host.config.Constants.PAN_CARD;
import static com.tulip.host.config.Constants.PROFILE_PICTURE;

import com.tulip.host.enums.BloodGroupEnum;
import com.tulip.host.enums.GenderEnum;
import com.tulip.host.enums.ReligionEnum;
import com.tulip.host.enums.StdEnum;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEditVM {

    @NotNull
    Long id;

    String type;
    String qualification;
    String address;
    BloodGroupEnum bloodGroup;
    String contact;
    boolean whatsappAvailable;
    boolean eveningClass;
    List<DependentVM> dependent;
    String experience;
    String aadhaar;
    List<FileUploadVM> aadhaarCard;
    List<FileUploadVM> panCard;
    List<FileUploadVM> birthCertificate;
    Date dob;
    GenderEnum gender;
    String name;
    String previousSchool;
    ReligionEnum religion;
    Long session;
    StdEnum std;
    private FileUploadVM profilePicture;

    public void setProfilePicture(List<FileUploadVM> profilePhoto) {
        FileUploadVM uploadVM = profilePhoto.stream().findFirst().orElse(null);
        if (uploadVM != null) {
            uploadVM.setDocumentType(PROFILE_PICTURE);
            this.profilePicture = uploadVM;
        }
    }

    public void setAadhaarCard(List<FileUploadVM> aadhaarCard) {
        this.aadhaarCard =
            aadhaarCard
                .stream()
                .map(item -> {
                    item.setDocumentType(AADHAAR_CARD);
                    return item;
                })
                .collect(Collectors.toList());
    }

    public void setPanCard(List<FileUploadVM> panCard) {
        this.panCard =
            panCard
                .stream()
                .map(item -> {
                    item.setDocumentType(PAN_CARD);
                    return item;
                })
                .collect(Collectors.toList());
    }

    public void setBirthCertificate(List<FileUploadVM> birthCertificate) {
        this.birthCertificate =
            birthCertificate
                .stream()
                .map(item -> {
                    item.setDocumentType(BIRTH_CERTIFICATE);
                    return item;
                })
                .collect(Collectors.toList());
    }
}
