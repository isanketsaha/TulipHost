package com.tulip.host.web.rest;

import com.tulip.host.data.EmployeeBasicDTO;
import com.tulip.host.data.EmployeeDetailsDTO;
import com.tulip.host.enums.UserRoleEnum;
import com.tulip.host.service.EmployeeService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;


    @RequestMapping("/all/active")
    public List<EmployeeBasicDTO> fetchActive(@RequestParam(required = false) UserRoleEnum role) {
        return employeeService.fetchAllEmployee(Boolean.TRUE, role);
    }

    @GetMapping("/all")
    public List<EmployeeBasicDTO> fetch() {
        return employeeService.fetchAllEmployee();
    }

    @GetMapping("/searchById/{id}")
    public EmployeeDetailsDTO search(@PathVariable int id) {
        return employeeService.searchEmployee(id);
    }

    @GetMapping("/searchByName/{name}")
    public List<EmployeeBasicDTO> search(@PathVariable String name) {
        return employeeService.searchEmployee(name);
    }

    @GetMapping("/searchByUserName/{userId}")
    public EmployeeBasicDTO searchByUserId(@PathVariable String userId) {
        return employeeService.searchByUserId(userId);
    }

    @RolesAllowed("UG_ADMIN")
    @RequestMapping("/edit")
    public EmployeeDetailsDTO edit() {
        return employeeService.editEmployee();
    }

    @PreAuthorize("hasAuthority('UG_PRINCIPAL') or hasAuthority('UG_ADMIN')")
    @GetMapping("/terminate")
    public void terminate(@Valid @RequestParam long id) {
        employeeService.terminate(id);
    }

    @PreAuthorize("hasAuthority('UG_PRINCIPAL') or hasAuthority('UG_ADMIN')")
    @GetMapping("/forgotPassword")
    public void forgotPassword(@Valid @RequestParam long id) {
        employeeService.forgotPassword(id);
    }

    @PreAuthorize("hasAuthority('UG_PRINCIPAL') or hasAuthority('UG_ADMIN')")
    @GetMapping("/joiningLetter")
    public String generateJoiningLetter(@RequestParam Long empId) throws IOException {
        return employeeService.fetchAppointment(empId);
    }
}
