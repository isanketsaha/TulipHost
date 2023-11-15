package com.tulip.host.web.rest.vm;

import static com.tulip.host.config.Constants.AADHAAR_CARD;

import com.tulip.host.enums.RelationEnum;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

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
    List<UploadVM> aadhaarCard;

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
}
