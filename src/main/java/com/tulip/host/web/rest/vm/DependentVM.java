package com.tulip.host.web.rest.vm;

import static com.tulip.host.config.Constants.AADHAAR_CARD;

import com.tulip.host.enums.RelationEnum;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DependentVM {

    Long id;
    String aadhaar;
    String contact;
    String name;
    String occupation;
    String qualification;
    RelationEnum relation;
    boolean whatsappAvailable;
    List<FileUploadVM> aadhaarCard;

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
}
