package com.tulip.host.web.rest;

import com.tulip.host.service.EmployeeService;
import com.tulip.host.service.StudentService;
import com.tulip.host.web.rest.vm.OnboardingVM;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @PostMapping
    public ResponseEntity<Long> onboard(@Valid @RequestBody OnboardingVM onboardingVM) throws Exception {
        if (onboardingVM.getType().equals("employee")) {
            return ResponseEntity.ok(employeeService.addEmployee(onboardingVM));
        } else if (onboardingVM.getType().equals("student")) {
            return ResponseEntity.ok(studentService.addStudent(onboardingVM));
        }
        throw new Exception("No Match found for onboard type.");
    }
}
