package com.tulip.host.web.rest.vm;

import com.tulip.host.enums.RelationEnum;
import java.util.List;
import javax.validation.constraints.NotNull;
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
}
