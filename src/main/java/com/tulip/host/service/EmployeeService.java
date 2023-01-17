package com.tulip.host.service;

import com.tulip.host.data.EmployeeBasicDTO;
import com.tulip.host.data.EmployeeDetailsDTO;
import com.tulip.host.domain.*;
import com.tulip.host.mapper.EmployeeMapper;
import com.tulip.host.repository.EmployeeRepository;
import com.tulip.host.repository.UserGroupRepository;
import com.tulip.host.repository.UserToDependentRepository;
import com.tulip.host.web.rest.vm.BankVM;
import com.tulip.host.web.rest.vm.DependentVM;
import com.tulip.host.web.rest.vm.InterviewVM;
import com.tulip.host.web.rest.vm.OnboardingVM;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    private final UserGroupRepository userGroupRepository;

    private final UserToDependentRepository userToDependentRepository;

    private final EmployeeMapper employeeMapper;

    public List<EmployeeBasicDTO> fetchAllEmployee(boolean isActive) {
        return employeeRepository.fetchAll(isActive);
    }

    public List<EmployeeBasicDTO> fetchAllEmployee() {
        return employeeRepository.fetchAll();
    }

    @Transactional
    public Long addEmployee(OnboardingVM employeeVM) throws Exception {
        UserGroup userGroupByAuthority = userGroupRepository.findUserGroupByAuthority(
            "UG_" + employeeVM.getInterview().getRole().name().toUpperCase()
        );
        if (userGroupByAuthority != null) {
            Employee employee = employeeMapper.toModel(employeeVM);
            employee.setGroup(userGroupByAuthority);
            Employee emp = employeeRepository.saveAndFlush(employee);
            return emp.getId();
        }
        throw new Exception("Unable to find usergroup");
    }

    public List<EmployeeBasicDTO> searchEmployee(String name) {
        return employeeRepository.searchByName(name);
    }

    public EmployeeDetailsDTO searchEmployee(long id) {
        Employee employee = employeeRepository.search(id);
        if (employee != null) {
            EmployeeDetailsDTO employeeDetailsDTO = employeeMapper.toEntity(employee);
            return employeeDetailsDTO;
        }
        return null;
    }

    public EmployeeDetailsDTO editEmployee() {
        return employeeRepository.edit();
    }
}
