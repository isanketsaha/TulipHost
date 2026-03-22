package com.tulip.host.web.rest;

import com.tulip.host.data.AboutContentDTO;
import com.tulip.host.data.ActivityItemDTO;
import com.tulip.host.data.BirthdayDTO;
import com.tulip.host.data.ClassListDTO;
import com.tulip.host.data.GalleryImageDTO;
import com.tulip.host.data.PublicSchoolInfoDTO;
import com.tulip.host.domain.Session;
import com.tulip.host.enums.UserRoleEnum;
import com.tulip.host.repository.StudentRepository;
import com.tulip.host.service.CalendarService;
import com.tulip.host.service.ClassroomService;
import com.tulip.host.service.ContentService;
import com.tulip.host.service.EmployeeService;
import com.tulip.host.service.GalleryService;
import com.tulip.host.service.SessionService;
import com.tulip.host.service.UploadService;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicController {

    private final SessionService sessionService;
    private final ClassroomService classroomService;
    private final EmployeeService employeeService;
    private final CalendarService calendarService;
    private final StudentRepository studentRepository;
    private final UploadService uploadService;
    private final GalleryService galleryService;
    private final ContentService contentService;

    @GetMapping("/content/about")
    public AboutContentDTO getAboutContent() {
        return contentService.getAboutContent();
    }

    @GetMapping("/content/activities")
    public List<ActivityItemDTO> getActivities() {
        return contentService.getActivities();
    }

    @GetMapping("/gallery")
    public List<GalleryImageDTO> getGallery() {
        return galleryService.getGalleryImages();
    }

    @GetMapping("/school-info")
    public PublicSchoolInfoDTO getSchoolInfo() {
        Session currentSession = sessionService.currentSession();

        List<ClassListDTO> classes = classroomService.fetchAllClasses(currentSession.getId());

        List<PublicSchoolInfoDTO.PublicTeacherDTO> teachers = employeeService
            .fetchAllEmployee(true, UserRoleEnum.TEACHER)
            .stream()
            .map(e ->
                PublicSchoolInfoDTO.PublicTeacherDTO.builder()
                    .id(e.getId())
                    .name(e.getName())
                    .role(e.getAuthority() != null ? e.getAuthority().replace("UG_", "") : null)
                    .qualification(e.getQualification())
                    .photoUrl(e.getProfilePictureUrl())
                    .build()
            )
            .toList();

        LocalDate today = LocalDate.now();
        List<PublicSchoolInfoDTO.CalendarEventDTO> events = calendarService
            .getAllEvents(today, today.plusDays(30))
            .stream()
            .map(e ->
                PublicSchoolInfoDTO.CalendarEventDTO.builder()
                    .name(e.getName())
                    .eventType(e.getEventType() != null ? e.getEventType().name() : null)
                    .startDate(e.getStartDate())
                    .endDate(e.getEndDate())
                    .build()
            )
            .toList();

        List<BirthdayDTO> birthdays = studentRepository
            .findActiveStudentsBirthdayData(currentSession.getId())
            .stream()
            .filter(row -> row[2] != null)
            .map(row -> {
                String studentName = (String) row[0];
                String std = (String) row[1];
                LocalDate dob = (LocalDate) row[2];
                String photoUid = (String) row[3];
                // Next birthday this year; if already passed, use next year
                LocalDate nextBirthday = dob.withYear(today.getYear());
                if (nextBirthday.isBefore(today)) {
                    nextBirthday = nextBirthday.plusYears(1);
                }
                int daysUntil = (int) ChronoUnit.DAYS.between(today, nextBirthday);
                return BirthdayDTO.builder()
                    .name(studentName)
                    .grade(std)
                    .today(daysUntil == 0)
                    .daysUntil(daysUntil)
                    .photoUrl(photoUid != null ? uploadService.getURL(photoUid) : null)
                    .build();
            })
            .sorted(Comparator.comparingInt(BirthdayDTO::getDaysUntil))
            .limit(6)
            .toList();

        return PublicSchoolInfoDTO.builder().classes(classes).teachers(teachers).events(events).birthdays(birthdays).build();
    }
}
