package com.tulip.host.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tulip.host.data.EmployeeLeaveDto;
import com.tulip.host.data.LeaveBalanceDTO;
import com.tulip.host.domain.ActionNotification;
import com.tulip.host.domain.Employee;
import com.tulip.host.domain.EmployeeLeave;
import com.tulip.host.domain.LeaveType;
import com.tulip.host.enums.LeaveStatus;
import com.tulip.host.enums.NotificationStatus;
import com.tulip.host.mapper.EmployeeLeaveMapper;
import com.tulip.host.mapper.LeaveBalanceMapper;
import com.tulip.host.mapper.LeaveTypeMapper;
import com.tulip.host.repository.ActionNotificationRepository;
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
    public final SessionService sessionService;
    public final EmployeeLeaveMapper employeeLeaveMapper;
    public final LeaveTypeMapper leaveTypeMapper;
    public final LeaveBalanceMapper leaveBalanceMapper;
    public final ActionNotificationRepository actionNotificationRepository;

    public List<EmployeeLeaveDto> getAllEmployeeLeavesAsDto(LeaveStatus status) {
        List<EmployeeLeave> employeeLeaves;
        if (status != null) {
            employeeLeaves = employeeLeaveRepository.findByStatus(status);
        } else {
            employeeLeaves = employeeLeaveRepository.findAll();
        }
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
        Employee employee = getEmployee(applyLeaveVM);
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

    public Employee getEmployee(ApplyLeaveVM applyLeaveVM) {
        Employee employee;
        if (applyLeaveVM.getEmployeeId() != null) {
            employee = employeeRepository.findById(applyLeaveVM.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Employee not found : " + applyLeaveVM.getEmployeeId()));
        } else {
            employee = employeeRepository.findByTid(applyLeaveVM.getTid())
                    .orElseThrow(() -> new RuntimeException("Employee not found : " + applyLeaveVM.getTid()));
        }
        return employee;
    }

    public EmployeeLeave updateEmployeeLeave(EmployeeLeave employeeLeave) {
        log.debug("Request to update EmployeeLeave : {}", employeeLeave);
        return employeeLeaveRepository.save(employeeLeave);
    }

    public EmployeeLeave updateStatus(Long leaveId, LeaveStatus status, String comments) {
        EmployeeLeave leave = employeeLeaveRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave not found : " + leaveId));
        leave.setStatus(status);
        leave.setComments(comments);
        return employeeLeaveRepository.save(leave);
    }

    public void deleteEmployeeLeave(Long id) {
        log.debug("Request to delete EmployeeLeave : {}", id);
        EmployeeLeave leave = employeeLeaveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found : " + id));

        // If leave is pending, delete the corresponding action notification
        if (leave.getStatus() == LeaveStatus.PENDING) {
            List<ActionNotification> notifications = actionNotificationRepository
                    .findByEntityTypeAndEntityId("LEAVE", id);
            notifications.stream()
                    .filter(n -> n.getStatus() == NotificationStatus.PENDING)
                    .forEach(actionNotificationRepository::delete);
            log.debug("Deleted {} pending action notification(s) for leave : {}", notifications.size(), id);
        }

        employeeLeaveRepository.deleteById(id);
    }

    public Map<String, List<LeaveBalanceDTO>> getBalance() {
        List<LeaveType> leaveTypes = leaveTypeRepository.findBySession(sessionService.currentSession());
        Map<String, List<LeaveBalanceDTO>> balance = employeeLeaveRepository.findLeaveBalance();

        balance.forEach((employeeName, leaveBalances) -> {
            if (leaveBalances.size() != leaveTypes.size()) {
                // Get IDs of leave types the employee already has
                Set<String> appliedLeaveType = leaveBalances.stream()
                        .map(LeaveBalanceDTO::getName)
                        .collect(Collectors.toSet());

                // Find missing leave types
                List<LeaveType> missingLeaveTypes = leaveTypes.stream()
                        .filter(lt -> !appliedLeaveType.contains(lt.getName()))
                        .collect(Collectors.toList());

                // Add missing leave types with 0 used days (full available balance)
                for (LeaveType missingLeaveType : missingLeaveTypes) {
                    LeaveBalanceDTO newBalance = leaveBalanceMapper.createLeaveBalance(missingLeaveType, null);
                    leaveBalances.add(newBalance);
                }
            }
        });

        return balance;
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
