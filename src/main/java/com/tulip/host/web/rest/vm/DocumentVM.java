package com.tulip.host.web.rest.vm;

import com.tulip.host.enums.DocumentCategoryEnum;
import com.tulip.host.enums.UploadEnum;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentVM {

    Long id;

    @NotNull
    private String name;

    @NotNull
    String description;

    @NotNull
    DocumentCategoryEnum documentCategory;

    Long session;

    Long std;

    @NotNull
    List<FileUploadVM> files;

    String createdBy;

    LocalDateTime createdDate;

}
