package com.tulip.host.web.rest;

import static com.tulip.host.config.Constants.ENROLLMENT_LETTER;
import static com.tulip.host.config.Constants.JOINING_LETTER;

import com.tulip.host.service.EmployeeService;
import com.tulip.host.service.StudentService;
import com.tulip.host.service.UploadService;
import com.tulip.host.web.rest.vm.FileUploadVM;
import com.tulip.host.web.rest.vm.OnboardingVM;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/onboarding")
@RequiredArgsConstructor
public class OnboardingController {

    private final EmployeeService employeeService;

    private final StudentService studentService;

    private final UploadService uploadService;

    @PostMapping
    public ResponseEntity<Long> onboard(@RequestBody OnboardingVM onboardingVM) throws Exception {
        if (onboardingVM.getType()
            .equals("employee")) {
            Long id = employeeService.addEmployee(onboardingVM);
            CompletableFuture.runAsync(() -> {
                try {
                    byte[] bytes = employeeService.generateJoiningLetter(id);
                    FileUploadVM joining_letter = uploadService.save(bytes, MediaType.APPLICATION_PDF_VALUE, JOINING_LETTER);
                    employeeService.attachEmployment(id, joining_letter);
                    employeeService.notifyOnboard(id);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to generate joining letter", e);
                }
            });
            return ResponseEntity.ok(id);
        } else if (onboardingVM.getType()
            .equals("student")) {
            Long id = studentService.addStudent(onboardingVM);
            CompletableFuture.runAsync(() -> {
                try {
                    byte[] bytes = studentService.generateEnrollmentLetter(id);
                    FileUploadVM enrollment_letter = uploadService.save(bytes, MediaType.APPLICATION_PDF_VALUE, ENROLLMENT_LETTER);
                    studentService.attachEnrollment(id, enrollment_letter);
                    studentService.notifyParent(id);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to generate enrollment letter", e);
                }
            });
            return ResponseEntity.ok(id);
        }
        throw new Exception("No Match found for onboard type.");
    }

    @Scheduled(cron = "0 01 17 26 * ?")
    public void notifyOnboard() {
        employeeService.notifyOnboard(58L);
    }
}
