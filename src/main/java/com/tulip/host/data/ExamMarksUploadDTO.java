package com.tulip.host.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamMarksUploadDTO {

    private Long id;
    private String examType;
    private String examName;
    private String uploadedBy;
    private String createdDate;
    private String fileUid;
}
