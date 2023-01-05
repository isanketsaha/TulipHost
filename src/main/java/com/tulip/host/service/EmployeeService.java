package com.tulip.host.service;

import com.tulip.host.data.EmployeeBasicDTO;
import com.tulip.host.data.EmployeeDetailsDTO;
import com.tulip.host.domain.*;
import com.tulip.host.mapper.EmployeeMapper;
import com.tulip.host.repository.DependentRepository;
import com.tulip.host.repository.EmployeeRepository;
import com.tulip.host.repository.UserGroupRepository;
import com.tulip.host.web.rest.vm.BankVM;
import com.tulip.host.web.rest.vm.DependentVM;
import com.tulip.host.web.rest.vm.InterviewVM;
import com.tulip.host.web.rest.vm.OnboardingVM;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    private final UserGroupRepository userGroupRepository;

    private final DependentRepository dependentRepository;

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
            "UG_" + employeeVM.getInterview().stream().findFirst().get().getRole().name().toUpperCase()
        );
        if (userGroupByAuthority != null) {
            Bank bank = buildBankModel(employeeVM.getBank().stream().findFirst().get());
            Interview interview = buildInterviewModel(employeeVM.getInterview().stream().findFirst().get());

            Employee employee = Employee
                .builder()
                .dob(employeeVM.getDob().toInstant())
                .qualification(employeeVM.getQualification())
                .gender(employeeVM.getGender().getDisplayType())
                .bloodGroup(employeeVM.getBloodGroup().getDisplayType())
                .religion(employeeVM.getReligion().name())
                .phoneNumber(String.valueOf(employeeVM.getContact()))
                .address(employeeVM.getAddress())
                .name(employeeVM.getName())
                .group(userGroupByAuthority)
                .bank(bank)
                .interview(interview)
                .build();
            List<Dependent> dependentList = employeeVM
                .getDependent()
                .stream()
                .map(dependentVM -> {
                    return buildDependentModel(dependentVM, employee);
                })
                .collect(Collectors.toList());
            List<Dependent> dependents = dependentRepository.saveAllAndFlush(dependentList);
            return dependents.stream().findFirst().get().getEmp().getId();
        }
        throw new Exception("Unable to find usergroup");
    }

    public List<EmployeeBasicDTO> searchEmployee(String name) {
        return employeeRepository.searchByName(name);
    }

    public EmployeeDetailsDTO searchEmployee(long id) {
        Employee employee = employeeRepository.findById(id).orElse(null);
        if (employee != null) {
            return employeeMapper.getEntityFromModel(employee);
        }
        return null;
    }

    public EmployeeDetailsDTO editEmployee() {
        return employeeRepository.edit();
    }

    private Interview buildInterviewModel(InterviewVM interviewVM) {
        return Interview
            .builder()
            .doj(interviewVM.getDoj())
            .interviewDate(interviewVM.getInterviewDate())
            .salary(interviewVM.getSalary())
            .comments(interviewVM.getComments())
            .build();
    }

    private Bank buildBankModel(BankVM bankVM) {
        return Bank
            .builder()
            .bankName(bankVM.getBankName())
            .accountNo(Long.valueOf(bankVM.getAccountNumber()))
            .ifsc(bankVM.getIfsc())
            .build();
    }

    Dependent buildDependentModel(DependentVM dependent, Employee employee) {
        return Dependent
            .builder()
            .contact(String.valueOf(dependent.getContact()))
            .name(dependent.getName())
            .occupation(dependent.getOccupation())
            .qualification(dependent.getQualification())
            .relationship(dependent.getRelation().name())
            .aadhaarNo(String.valueOf(dependent.getAadhaar()))
            .emp(employee)
            .build();
    }
}
