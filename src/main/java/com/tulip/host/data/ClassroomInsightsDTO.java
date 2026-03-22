package com.tulip.host.data;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClassroomInsightsDTO {

    private List<ExamInsightDTO> exams;
    private List<BehaviourSubjectInsightDTO> behaviour;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExamInsightDTO {

        private String examName;
        private String examType;
        private int totalStudents;
        private int passCount;
        private double passPercent;
        private List<SubjectInsightDTO> subjects;
        private List<StudentInsightDTO> lowPerformers;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubjectInsightDTO {

        private String subjectKey;
        private double avgPercent;
        private int passCount;
        private int totalCount;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StudentInsightDTO {

        private Long studentId;
        private String studentName;
        private double scorePercent;
        private List<String> weakSubjects;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BehaviourSubjectInsightDTO {

        private String subjectKey;
        private int weekCount;
        private double avgScore;
        private List<BehaviourStudentInsightDTO> lowScoreStudents;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BehaviourStudentInsightDTO {

        private Long studentId;
        private String studentName;
        private double avgScore;
    }
}
