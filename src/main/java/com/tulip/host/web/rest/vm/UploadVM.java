package com.tulip.host.web.rest.vm;

import com.tulip.host.enums.UploadTypeEnum;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadVM {

    private UploadTypeEnum type;
    FileUploadVM file;

    public void setFile(List<FileUploadVM> ele) {
        FileUploadVM item = ele.stream().findFirst().orElseThrow();
        item.setDocumentType(type.name() + "_" + "UPLOAD");
        this.file = item;
    }
}
