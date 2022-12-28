package com.tulip.host.service;

import com.tulip.host.data.EmployeeBasicDTO;
import com.tulip.host.data.EmployeeDetailsDTO;
import com.tulip.host.domain.Employee;
import com.tulip.host.domain.UserGroup;
import com.tulip.host.repository.EmployeeRepository;
import com.tulip.host.repository.UserGroupRepository;
import com.tulip.host.web.rest.vm.AddEmployeeVM;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    private final UserGroupRepository userGroupRepository;

    public List<EmployeeBasicDTO> fetchAllEmployee(boolean isActive) {
        return employeeRepository.fetchAll(isActive);
    }

    public List<EmployeeBasicDTO> fetchAllEmployee() {
        return employeeRepository.fetchAll();
    }

    public void addEmployee(AddEmployeeVM employeeVM) {
        UserGroup userGroupByAuthority = userGroupRepository.findUserGroupByAuthority("UG_" + employeeVM.getEmployeeType().toUpperCase());
        if (userGroupByAuthority != null) {
            Employee employee = Employee
                .builder()
                .dob(employeeVM.getDob())
                .highestQualification(employeeVM.getHighestQualification())
                .gender(employeeVM.getGender().getDisplayType())
                .bloodGroup(employeeVM.getBloodGroup().getDisplayType())
                .religion(employeeVM.getReligion())
                .phoneNumber(employeeVM.getPhoneNumber())
                .address(employeeVM.getAddress())
                .name(employeeVM.getName())
                .father(employeeVM.getFather())
                .groupId(userGroupByAuthority.getId())
                .active(Boolean.TRUE)
                .build();
            employeeRepository.add(employee);
        }
    }

    public List<EmployeeBasicDTO> searchEmployee(String name) {
        return employeeRepository.searchByName(name);
    }

    public EmployeeDetailsDTO searchEmployee(int id) {
        return employeeRepository.search(id);
    }

    public EmployeeDetailsDTO editEmployee() {
        return employeeRepository.edit();
    }
}
