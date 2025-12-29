package com.tulip.host.service;

import com.tulip.host.data.StudentDetailsDTO;
import com.tulip.host.domain.ClassDetail;
import com.tulip.host.domain.Session;
import com.tulip.host.domain.Student;
import com.tulip.host.enums.CommunicationChannel;
import com.tulip.host.repository.ClassDetailRepository;
import com.tulip.host.service.communication.CommunicationRequest;
import com.tulip.host.service.communication.OutboundCommunicationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledService {

    private final StudentService studentService;
    private final ClassDetailRepository classDetailRepository;
    private final SessionService sessionService;
    private final OutboundCommunicationService outboundCommunicationService;
    private final MailService mailService;

    @Scheduled(cron = "0 44 17 27 * ?")
    @Transactional
    public void notifyOnFeesDues() {
        log.info("Starting scheduled task: notifyOnFeesDues");
        Session session = sessionService.currentSession();
        List<ClassDetail> allBySessionId = classDetailRepository.findAllBySessionId(session.getId());
        Collections.sort(allBySessionId, Comparator.comparing(ClassDetail::getStd));
        allBySessionId.stream()
            .forEach(std -> {
                Map<Long, Integer> longIntegerMap = studentService.calculatePendingMonthFeesBatch(std.getStudents()
                        .stream()
                        .map(Student::getId)
                        .toList(),
                    std.getId(), session);
                longIntegerMap.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue() > 2)
                    .limit(2)
                    .forEach(entry -> sendNotification(entry.getKey()));
            });
    }

    private void sendNotification(Long studentId) {
        StudentDetailsDTO studentDetailsDTO = studentService.searchStudent(studentId);
        Map<String, Object> map = Map.of("studentName", studentDetailsDTO.getName(),
            "className", studentDetailsDTO.getClassDetails()
                .stream()
                .findFirst()
                .get()
                .getStd());
        outboundCommunicationService.send(CommunicationRequest.builder()
            .channel(CommunicationChannel.SMS)
            .recipient(new String[]{studentDetailsDTO.getPhoneNumber()})
            .content(mailService.renderTemplate("mail/due.vm", map))
            .subject("FEES_DUES_NOTIFICATION")
            .entityType("STUDENT")
            .entityId(studentDetailsDTO.getId())
            .build());
    }

}
