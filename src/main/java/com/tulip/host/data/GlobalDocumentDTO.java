package com.tulip.host.data;

import com.tulip.host.enums.GlobalDocumentCategory;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GlobalDocumentDTO {

    private Long id;
    private String name;
    private String description;
    private GlobalDocumentCategory category;
    private String downloadUrl;
}
