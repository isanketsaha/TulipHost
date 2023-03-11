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
public class OnboardingVM {

    @NotNull
    String name;

    @NotNull
    String type;

    String qualification;

    @NotNull
    String address;

    BloodGroupEnum bloodGroup;

    @NotNull
    String contact;

    boolean whatsappAvailable;

    @NotNull
    List<DependentVM> dependent;

    String experience;

    @NotNull
    Date dob;

    Boolean eveningClass;

    GenderEnum gender;
    CredentialVM credential;
    List<UploadVM> aadhaarCard;
    List<UploadVM> panCard;
    List<UploadVM> birthCertificate;
    String previousSchool;
    ReligionEnum religion;
    Long session;
    BankVM bank;
    InterviewVM interview;
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
