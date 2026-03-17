package com.tulip.host.data;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AcademicUploadDTO {

    private Long id;
    private Long classroomId;
    private String subjectKey;
    private LocalDate weekStartDate;
    private String uploadType;
    private String fileUid;
    private String uploadedBy;
    private String createdDate;
}
