package com.tulip.host.service;

import com.tulip.host.data.EmployeeBasicDTO;
import com.tulip.host.data.EmployeeDetailsDTO;
import com.tulip.host.data.LoginDTO;
import com.tulip.host.domain.*;
import com.tulip.host.enums.UserRoleEnum;
import com.tulip.host.mapper.CredentialMapper;
import com.tulip.host.mapper.EmployeeMapper;
import com.tulip.host.repository.CredentialRepository;
import com.tulip.host.repository.EmployeeRepository;
import com.tulip.host.repository.SessionRepository;
import com.tulip.host.repository.UserGroupRepository;
import com.tulip.host.repository.UserToDependentRepository;
import com.tulip.host.web.rest.vm.BankVM;
import com.tulip.host.web.rest.vm.CredentialVM;
import com.tulip.host.web.rest.vm.DependentVM;
import com.tulip.host.web.rest.vm.InterviewVM;
import com.tulip.host.web.rest.vm.OnboardingVM;
import java.util.Date;
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
    private final CredentialMapper credentialMapper;

    private final UserGroupRepository userGroupRepository;

    private final UserToDependentRepository userToDependentRepository;

    private final EmployeeMapper employeeMapper;
    private final CredentialRepository credentialRepository;
    private final SessionRepository sessionRepository;

    @Transactional
    public List<EmployeeBasicDTO> fetchAllEmployee(boolean isActive) {
        Long id = sessionRepository.fetchCurrentSession().getId();
        List<Employee> employees = employeeRepository.fetchAll(isActive);
        return employeeMapper.toBasicEntityList(employees);
    }

    @Transactional
    public List<EmployeeBasicDTO> fetchAllEmployee() {
        List<Employee> employees = employeeRepository.fetchAll();
        return employeeMapper.toBasicEntityList(employees);
    }

    @Transactional
    public Long addEmployee(OnboardingVM employeeVM) throws Exception {
        UserGroup userGroupByAuthority = userGroupRepository.findUserGroupByAuthority(employeeVM.getInterview().getRole().getValue());
        if (userGroupByAuthority != null) {
            Employee employee = employeeMapper.toModel(employeeVM);
            employee.setLocked(userGroupByAuthority.getAuthority().equalsIgnoreCase(UserRoleEnum.TEACHER.getValue()));
            employee.setResetCredential(!userGroupByAuthority.getAuthority().equalsIgnoreCase(UserRoleEnum.TEACHER.getValue()));
            employee.setGroup(userGroupByAuthority);
            Credential credential = credentialMapper.toEntity(
                CredentialVM.builder().password("tulip123").userId(employeeVM.getInterview().getUserId()).build()
            );
            employee.setCredential(credential);
            Employee emp = employeeRepository.saveAndFlush(employee);
            return emp.getId();
        }
        throw new Exception("Unable to find usergroup");
    }

    @Transactional
    public List<EmployeeBasicDTO> searchEmployee(String name) {
        List<Employee> employees = employeeRepository.searchByName(name);
        return employeeMapper.toBasicEntityList(employees);
    }

    @javax.transaction.Transactional
    public EmployeeDetailsDTO searchEmployee(long id) {
        Employee employee = employeeRepository.search(id);
        if (employee != null) {
            EmployeeDetailsDTO employeeDetailsDTO = employeeMapper.toEntity(employee);
            return employeeDetailsDTO;
        }
        return null;
    }

    @Transactional
    public EmployeeDetailsDTO editEmployee() {
        return employeeRepository.edit();
    }

    public void deactivate(long id) {
        Employee byId = employeeRepository.findById(id).orElse(null);
        if (byId != null) {
            byId.setActive(Boolean.FALSE);
            //            byId.setTerminationDate(new Date());
            employeeRepository.save(byId);
        }
    }
}
