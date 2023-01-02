package com.tulip.host.web.rest;

import com.tulip.host.data.EmployeeBasicDTO;
import com.tulip.host.data.EmployeeDetailsDTO;
import com.tulip.host.service.EmployeeService;
import com.tulip.host.web.rest.vm.AddEmployeeVM;
import com.tulip.host.web.rest.vm.OnboardingVM;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @RequestMapping("/all/active")
    public List<EmployeeBasicDTO> fetchActive() {
        return employeeService.fetchAllEmployee(Boolean.TRUE);
    }

    @RequestMapping("/all")
    public List<EmployeeBasicDTO> fetch() {
        return employeeService.fetchAllEmployee();
    }

    @RequestMapping("/searchById/{id}")
    public EmployeeDetailsDTO search(@PathVariable int id) {
        return employeeService.searchEmployee(id);
    }

    @RequestMapping("/searchByName/{name}")
    public List<EmployeeBasicDTO> search(@PathVariable String name) {
        return employeeService.searchEmployee(name);
    }

    @RolesAllowed("UG_ADMIN")
    @RequestMapping("/edit")
    public EmployeeDetailsDTO edit() {
        return employeeService.editEmployee();
    }
}
