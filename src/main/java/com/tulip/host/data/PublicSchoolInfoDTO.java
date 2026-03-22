package com.tulip.host.data;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PublicSchoolInfoDTO {

    private List<ClassListDTO> classes;

    private List<PublicTeacherDTO> teachers;

    private List<CalendarEventDTO> events;

    private List<BirthdayDTO> birthdays;

    @Data
    @Builder
    public static class PublicTeacherDTO {

        private Long id;
        private String name;
        private String role;
        private String qualification;
        private String photoUrl;
    }

    @Data
    @Builder
    public static class CalendarEventDTO {

        private String name;
        private String eventType;
        private LocalDate startDate;
        private LocalDate endDate;
    }
}
