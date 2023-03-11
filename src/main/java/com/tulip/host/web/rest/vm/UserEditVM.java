package com.tulip.host.web.rest.vm;

import static com.tulip.host.config.Constants.AADHAAR_CARD;
import static com.tulip.host.config.Constants.BIRTH_CERTIFICATE;
import static com.tulip.host.config.Constants.PAN_CARD;

import com.tulip.host.enums.BloodGroupEnum;
import com.tulip.host.enums.GenderEnum;
import com.tulip.host.enums.ReligionEnum;
import com.tulip.host.enums.StdEnum;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
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
    List<DependentVM> dependent;
    String experience;

    List<UploadVM> aadhaarCard;
    List<UploadVM> panCard;
    List<UploadVM> birthCertificate;
    Date dob;
    GenderEnum gender;
    String name;
    String previousSchool;
    ReligionEnum religion;
    Long session;
    StdEnum std;

    public void setAadhaarCard(List<UploadVM> aadhaarCard) {
        this.aadhaarCard =
            aadhaarCard
                .stream()
                .map(item -> {
                    item.setDocumentType(AADHAAR_CARD);
                    return item;
                })
                .collect(Collectors.toList());
    }

    public void setPanCard(List<UploadVM> panCard) {
        this.panCard =
            panCard
                .stream()
                .map(item -> {
                    item.setDocumentType(PAN_CARD);
                    return item;
                })
                .collect(Collectors.toList());
    }

    public void setBirthCertificate(List<UploadVM> birthCertificate) {
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