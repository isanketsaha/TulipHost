package com.tulip.host.web.rest;

import static com.tulip.host.config.Constants.JOINING_LETTER;

import com.tulip.host.service.EmployeeService;
import com.tulip.host.service.StudentService;
import com.tulip.host.service.UploadService;
import com.tulip.host.web.rest.vm.OnboardingVM;
import com.tulip.host.web.rest.vm.UploadVM;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/onboarding")
@RequiredArgsConstructor
public class OnboardingController {

    private final EmployeeService employeeService;

    private final StudentService studentService;

    private final UploadService uploadService;

    @PostMapping
    public ResponseEntity<Long> onboard(@RequestBody OnboardingVM onboardingVM) throws Exception {
        if (onboardingVM.getType().equals("employee")) {
            Long id = employeeService.addEmployee(onboardingVM);
            byte[] bytes = employeeService.generateJoiningLetter(id);
            UploadVM joining_letter = uploadService.save(bytes, MediaType.APPLICATION_PDF_VALUE, JOINING_LETTER);
            employeeService.attachEmployment(id, joining_letter);
            return ResponseEntity.ok(id);
        } else if (onboardingVM.getType().equals("student")) {
            return ResponseEntity.ok(studentService.addStudent(onboardingVM));
        }
        throw new Exception("No Match found for onboard type.");
    }
}
