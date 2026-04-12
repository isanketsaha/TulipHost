package com.tulip.host.data;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlannerDataDTO {

    private Long uploadId;
    private Long classroomId;
    private String subjectKey;
    private LocalDate weekStartDate;
    private String topicSubTopic;
    private String teachingAids;
    private String activityExplanation;
    private String activityLearningObjective;
    private String learningOutcome;
    private String evaluation;
    private String homeworkWorksheet;
    private String uploadedBy;
    private String fileUid;
}
