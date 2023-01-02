package com.tulip.host.web.rest;

import com.tulip.host.service.EmployeeService;
import com.tulip.host.service.StudentService;
import com.tulip.host.web.rest.vm.OnboardingVM;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/onboarding")
@RequiredArgsConstructor
public class OnboardingController {

    EmployeeService employeeService;

    StudentService studentService;

    @PostMapping
    public void onboard(@Valid @RequestBody OnboardingVM onboardingVM) {
        if (onboardingVM.getType().equals("employee")) {
            employeeService.addEmployee(onboardingVM);
        } else if (onboardingVM.getType().equals("student")) {
            studentService.addStudent(onboardingVM);
        }
    }
}
