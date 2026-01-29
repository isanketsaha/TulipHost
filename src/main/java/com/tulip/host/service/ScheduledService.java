package com.tulip.host.service;

import com.tulip.host.data.AttendanceSummaryDTO;
import com.tulip.host.data.StudentDetailsDTO;
import com.tulip.host.domain.ClassDetail;
import com.tulip.host.domain.Inventory;
import com.tulip.host.domain.Session;
import com.tulip.host.domain.Student;
import com.tulip.host.enums.CommunicationChannel;
import com.tulip.host.repository.ClassDetailRepository;
import com.tulip.host.repository.InventoryRepository;
import com.tulip.host.service.communication.CommunicationRequest;
import com.tulip.host.service.communication.OutboundCommunicationService;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledService {

    private final StudentService studentService;
    private final ClassDetailRepository classDetailRepository;
    private final SessionService sessionService;
    private final OutboundCommunicationService outboundCommunicationService;
    private final MailService mailService;
    private final EntityManager em;
    private final eOfficeApiService eOfficeApiService;

    @Scheduled(cron = "0 30 14 2 * ?")
    @Transactional
    public void notifyOnFeesDues() {
        log.info("Starting scheduled task: notifyOnFeesDues");
        org.hibernate.Session unwrap = em.unwrap(org.hibernate.Session.class);
        Session session = sessionService.currentSession();
        unwrap.enableFilter("activeStudent").setParameter("flag", true);
        List<ClassDetail> allBySessionId = classDetailRepository.findAllBySessionId(session.getId());
        Collections.sort(allBySessionId, Comparator.comparing(ClassDetail::getStd));
        allBySessionId
            .stream()
            .forEach(std -> {
                Map<Long, Integer> longIntegerMap = studentService.calculatePendingMonthFeesBatch(
                    std.getStudents().stream().map(Student::getId).toList(),
                    std.getId(),
                    session
                );
                longIntegerMap
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getValue() > 2)
                    .forEach(entry -> {
                        log.info("Sending dues notification to studentId={} with pending months={}", entry.getKey(), entry.getValue());
                    });
            });
        unwrap.disableFilter("activeStudent");
    }

    private void sendNotification(Long studentId) {
        StudentDetailsDTO studentDetailsDTO = studentService.searchStudent(studentId);
        Map<String, Object> map = Map.of(
            "studentName",
            studentDetailsDTO.getName(),
            "className",
            studentDetailsDTO.getClassDetails().stream().findFirst().orElseThrow().getStd()
        );
        outboundCommunicationService.send(
            CommunicationRequest.builder()
                .channel(CommunicationChannel.SMS)
                .recipient(new String[] { studentDetailsDTO.getPhoneNumber() })
                .content(mailService.renderTemplate("mail/due.vm", map))
                .subject("FEES_DUES_NOTIFICATION")
                .entityType("STUDENT")
                .entityId(studentDetailsDTO.getId())
                .build()
        );
    }

    @Scheduled(cron = "* * * 2 * ?")
    @Transactional
    public void createAttendance() {
        log.info("Starting scheduled task: createAttendance");
        try {
            LocalDate month = LocalDate.now().minusMonths(1);
            LocalDate lastMonthStart = month.withDayOfMonth(1);
            LocalDate lastMonthEnd = month.with(TemporalAdjusters.lastDayOfMonth());

            log.debug("Fetching attendance data for period: {} to {}", lastMonthStart, lastMonthEnd);

            Map<String, AttendanceSummaryDTO> attendanceSummary = eOfficeApiService.getTimesheetSummary(
                "ALL",
                lastMonthStart,
                lastMonthEnd
            );

            if (attendanceSummary.isEmpty()) {
                log.warn("No attendance records found for the period {} to {}", lastMonthStart, lastMonthEnd);
                return;
            }
            log.debug("Successfully fetched attendance data for {} employees", attendanceSummary.size());
            Map<String, Object> model = Map.of("employees", attendanceSummary.values().stream().toList());
            String[] to = new String[] { "sanketsaha@gmail.com", "principal@tulipschool.co.in" };
            outboundCommunicationService.send(
                CommunicationRequest.builder()
                    .channel(com.tulip.host.enums.CommunicationChannel.EMAIL)
                    .recipient(to)
                    .subject("Attendance for Month - " + month.getMonth().name() + "/" + month.getYear())
                    .content(mailService.renderTemplate("mail/salary.vm", model))
                    .entityType(AttendanceSummaryDTO.class.getName())
                    .build()
            );

            log.info("Completed scheduled task: createAttendance");
        } catch (Exception e) {
            log.error("Error in createAttendance scheduled task", e);
        }
    }
}
