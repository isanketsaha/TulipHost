package com.tulip.host.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tulip.host.data.EmployeeLeaveDto;
import com.tulip.host.data.LeaveBalanceDTO;
import com.tulip.host.domain.Employee;
import com.tulip.host.domain.EmployeeLeave;
import com.tulip.host.domain.LeaveType;
import com.tulip.host.mapper.EmployeeLeaveMapper;
import com.tulip.host.mapper.LeaveTypeMapper;
import com.tulip.host.repository.EmployeeLeaveRepository;
import com.tulip.host.repository.EmployeeRepository;
import com.tulip.host.repository.LeaveTypeRepository;
import com.tulip.host.web.rest.vm.ApplyLeaveVM;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EmployeeLeaveService {

    public final EmployeeLeaveRepository employeeLeaveRepository;
    public final EmployeeRepository employeeRepository;
    public final LeaveTypeRepository leaveTypeRepository;
    public final EmployeeLeaveMapper employeeLeaveMapper;
    public final LeaveTypeMapper leaveTypeMapper;

    public List<EmployeeLeaveDto> getAllEmployeeLeavesAsDto() {
        List<EmployeeLeave> employeeLeaves = employeeLeaveRepository.findAll();
        return employeeLeaves.stream()
                .map(employeeLeaveMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<EmployeeLeaveDto> getEmployeeLeavesByDateRangeAsDto(String fromDate, String toDate) {
        List<EmployeeLeave> employeeLeaves = employeeLeaveRepository.findByDateRange(fromDate, toDate);
        return employeeLeaves.stream()
                .map(employeeLeaveMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<EmployeeLeaveDto> getEmployeeLeaveByEmployeeIdAsDto(Long employeeId) {
        List<EmployeeLeave> employeeLeaves = employeeLeaveRepository.findByEmployeeId(employeeId);
        return employeeLeaves.stream()
                .map(employeeLeaveMapper::toDto)
                .collect(Collectors.toList());
    }

    public EmployeeLeave createEmployeeLeaveFromVM(ApplyLeaveVM applyLeaveVM) {
        Employee employee = null;
        if (applyLeaveVM.getEmployeeId() != null) {
            employee = employeeRepository.findById(applyLeaveVM.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Employee not found : " + applyLeaveVM.getEmployeeId()));
        } else {
            employee = employeeRepository.findByTid(applyLeaveVM.getTid())
                    .orElseThrow(() -> new RuntimeException("Employee not found : " + applyLeaveVM.getTid()));
        }
        // Find leave type
        LeaveType leaveType = leaveTypeRepository.findById(applyLeaveVM.getLeaveTypeId())
                .orElseThrow(() -> new RuntimeException("Leave type not found : " + applyLeaveVM.getLeaveTypeId()));
        EmployeeLeave employeeLeave = employeeLeaveMapper.toEntity(applyLeaveVM);
        employeeLeave.setEmployee(employee);
        employeeLeave.setLeaveType(leaveType);
        if (validateLeaveBalance(employee.getId(), employeeLeave)) {

            return employeeLeaveRepository.save(employeeLeave);
        }
        throw new RuntimeException("Leave balance not available");
    }

    public EmployeeLeave updateEmployeeLeave(EmployeeLeave employeeLeave) {
        log.debug("Request to update EmployeeLeave : {}", employeeLeave);
        return employeeLeaveRepository.save(employeeLeave);
    }

    public void deleteEmployeeLeave(Long id) {
        log.debug("Request to delete EmployeeLeave : {}", id);
        employeeLeaveRepository.deleteById(id);
    }

    public List<LeaveBalanceDTO> getBalanceByEmpId(Long employeeId) {
        return employeeLeaveRepository.findLeaveBalance(employeeId);
    }

    public List<LeaveBalanceDTO> getBalanceByTid(String tid) {
        return employeeLeaveRepository.findLeaveBalanceByTid(tid);
    }

    public boolean validateLeaveBalance(Long employeeId, EmployeeLeave employeeLeave) {
        List<LeaveBalanceDTO> leaveBalances = employeeLeaveRepository.findLeaveBalance(employeeId);

        for (LeaveBalanceDTO balance : leaveBalances) {
            if (employeeLeave.getLeaveType().getId().equals(balance.getId())
                    && balance.getAvailableCount() >= employeeLeave.getTotalDays().doubleValue()) {
                return true;
            }
    }

    return false;
    }

    public List<EmployeeLeave> getAppliedLeaveByEmpId(Long employeeId) {
        return employeeLeaveRepository.findByEmployeeId(employeeId);
    }
}
