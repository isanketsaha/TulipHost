package com.tulip.host.service;

import com.tulip.host.domain.EmployeeLeave;
import com.tulip.host.domain.LeaveType;
import com.tulip.host.repository.EmployeeLeaveRepository;
import com.tulip.host.repository.LeaveTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
import java.util.ArrayList;
import com.tulip.host.domain.Session;
import com.tulip.host.repository.impl.EmployeeLeaveRepositoryImpl;
import com.tulip.host.domain.Employee;
import com.tulip.host.repository.EmployeeRepository;
import com.tulip.host.web.rest.vm.ApplyLeaveVM;
import com.tulip.host.mapper.EmployeeLeaveMapper;
import com.tulip.host.data.LeaveBalanceDTO;

@Service
@AllArgsConstructor
public class EmployeeLeaveService {

    private final EmployeeLeaveRepository employeeLeaveRepository;
    private final EmployeeLeaveRepositoryImpl employeeLeaveRepositoryImpl;
    private final LeaveTypeRepository leaveTypeRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeLeaveMapper employeeLeaveMapper;

    public EmployeeLeave createEmployeeLeave(EmployeeLeave employeeLeave) {
        validateLeaveType(employeeLeave.getLeaveType().getId());
        employeeLeave.setCreatedDate(LocalDateTime.now());
        employeeLeave.setLastModifiedDate(LocalDateTime.now());
        return employeeLeaveRepository.save(employeeLeave);
    }

    public List<EmployeeLeave> getAllEmployeeLeaves() {
        return employeeLeaveRepository.findAll();
    }

    public EmployeeLeave updateEmployeeLeave(Long id, EmployeeLeave updatedEmployeeLeave) {
        EmployeeLeave existingEmployeeLeave = employeeLeaveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("EmployeeLeave not found with id: " + id));

        validateLeaveType(updatedEmployeeLeave.getLeaveType().getId());

        // Use MapStruct to update the entity
        employeeLeaveMapper.updateEntityFromEntity(updatedEmployeeLeave, existingEmployeeLeave);
        existingEmployeeLeave.setLastModifiedDate(LocalDateTime.now());

        return employeeLeaveRepository.save(existingEmployeeLeave);
    }

    public void deleteEmployeeLeave(Long id) {
        if (employeeLeaveRepository.existsById(id)) {
            employeeLeaveRepository.deleteById(id);
        } else {
            throw new RuntimeException("EmployeeLeave not found with id: " + id);
        }
    }

    private void validateLeaveType(Long leaveTypeId) {
        if (!leaveTypeRepository.existsById(leaveTypeId)) {
            throw new RuntimeException("LeaveType not found with id: " + leaveTypeId);
        }
    }

    public List<LeaveBalanceDTO> getBalanceByEmpId(String employeeId) {
        // Get current session from repository implementation
        Session currentSession = employeeLeaveRepositoryImpl.getCurrentSession();

        // Get leave types for current session
        List<LeaveType> allLeaveTypes = leaveTypeRepository.findBySession(currentSession);

        // Get used leaves for this employee (already filtered by current session in
        // repository)
        Map<String, Long> usedLeaves = employeeLeaveRepository.findLeaveBalance(employeeId);

        // Calculate available balance for each leave type
        List<LeaveBalanceDTO> leaveBalances = new ArrayList<>();

        for (LeaveType leaveType : allLeaveTypes) {
            String leaveTypeName = leaveType.getName();
            int totalAllowed = leaveType.getCount();
            long usedCount = usedLeaves.getOrDefault(leaveTypeName, 0L);
            int available = totalAllowed - (int) usedCount;

            LeaveBalanceDTO balanceDTO = new LeaveBalanceDTO(
                    leaveType.getId(),
                    leaveTypeName,
                    available,
                    totalAllowed,
                    usedCount);
            leaveBalances.add(balanceDTO);
        }

        return leaveBalances;
    }

    public EmployeeLeave createEmployeeLeaveFromVM(ApplyLeaveVM applyLeaveVM) {
        // Find employee by employeeId (string)
        Employee employee = employeeRepository.findByUserId(applyLeaveVM.getEmployeeId())
                .orElseThrow(
                        () -> new RuntimeException("Employee not found with userId: " + applyLeaveVM.getEmployeeId()));

        // Find leave type by id
        LeaveType leaveType = leaveTypeRepository.findById(applyLeaveVM.getLeaveTypeId())
                .orElseThrow(
                        () -> new RuntimeException("LeaveType not found with id: " + applyLeaveVM.getLeaveTypeId()));

        // Map DTO to entity using MapStruct
        EmployeeLeave employeeLeave = employeeLeaveMapper.toEntity(applyLeaveVM);

        // Set manually mapped fields
        employeeLeave.setEmployee(employee);
        employeeLeave.setLeaveType(leaveType);
        employeeLeave.setCreatedDate(LocalDateTime.now());
        employeeLeave.setLastModifiedDate(LocalDateTime.now());

        return employeeLeaveRepository.save(employeeLeave);
    }

    public List<EmployeeLeave> getEmployeeLeaveByEmployeeId(String employeeId) {
        return employeeLeaveRepository.findByEmployeeId(employeeId);
    }
}
