package com.tulip.host.web.rest;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tulip.host.data.EmployeeLeaveDto;
import com.tulip.host.data.LeaveBalanceDTO;
import com.tulip.host.domain.EmployeeLeave;
import com.tulip.host.domain.LeaveType;
import com.tulip.host.service.EmployeeLeaveService;
import com.tulip.host.service.LeaveTypeService;
import com.tulip.host.web.rest.vm.ApplyLeaveVM;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/leave")
public class LeaveManagementController {
    @Autowired
    private LeaveTypeService leaveTypeService;

    @Autowired
    private EmployeeLeaveService employeeLeaveService;

    // LeaveType Endpoints
    @PostMapping("/types")
    public ResponseEntity<LeaveType> createLeaveType(@RequestBody LeaveType leaveType) {
        return ResponseEntity.ok(leaveTypeService.createLeaveType(leaveType));
    }

    @GetMapping("/types/{id}")
    public ResponseEntity<LeaveType> getLeaveTypeById(@PathVariable Long id) {
        Optional<LeaveType> leaveType = leaveTypeService.getLeaveType(id);
        return leaveType.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/balance/{employeeId}")
    public ResponseEntity<List<LeaveBalanceDTO>> getBalanceByEmpId(@PathVariable Long employeeId) {
        return ResponseEntity.ok(employeeLeaveService.getBalanceByEmpId(employeeId));
    }

    @GetMapping("/balance/tid/{tid}")
    public ResponseEntity<List<LeaveBalanceDTO>> getBalanceByTid(@PathVariable String tid) {
        return ResponseEntity.ok(employeeLeaveService.getBalanceByTid(tid));
    }

    @GetMapping("/appliedLeave/{employeeId}")
    public ResponseEntity<List<EmployeeLeave>> getAppliedLeaveByEmpId(@PathVariable Long employeeId) {
        return ResponseEntity.ok(employeeLeaveService.getAppliedLeaveByEmpId(employeeId));
    }

    @GetMapping("/types")
    public ResponseEntity<List<LeaveType>> getAllLeaveTypes() {
        return ResponseEntity.ok(leaveTypeService.getAllLeaveTypes());
    }

    @PutMapping("/types/{id}")
    public ResponseEntity<LeaveType> updateLeaveType(@PathVariable Long id, @RequestBody LeaveType leaveType) {
        leaveType.setId(id);
        return ResponseEntity.ok(leaveTypeService.updateLeaveType(leaveType));
    }

    @DeleteMapping("/types/{id}")
    public ResponseEntity<Void> deleteLeaveType(@PathVariable Long id) {
        leaveTypeService.deleteLeaveType(id);
        return ResponseEntity.ok().build();
    }

    // EmployeeLeave Endpoints
    @PostMapping("/apply")
    public ResponseEntity<EmployeeLeave> applyLeave(@Valid @RequestBody ApplyLeaveVM applyLeaveVM) {
        return ResponseEntity.ok(employeeLeaveService.createEmployeeLeaveFromVM(applyLeaveVM));
    }

    @GetMapping("/applied-leaves/{employeeId}")
    public ResponseEntity<List<EmployeeLeaveDto>> getEmployeeLeaveByEmployeeId(@PathVariable String employeeId) {
        if ("ALL".equals(employeeId)) {
            List<EmployeeLeaveDto> allEmployeeLeaves = employeeLeaveService.getAllEmployeeLeavesAsDto();
            return ResponseEntity.ok(allEmployeeLeaves);
        } else {
            List<EmployeeLeaveDto> employeeLeaves = employeeLeaveService
                    .getEmployeeLeaveByEmployeeIdAsDto(Long.parseLong(employeeId));
            return ResponseEntity.ok(employeeLeaves);
        }
    }

    @GetMapping("/applied-leaves")
    public ResponseEntity<List<EmployeeLeaveDto>> getAllEmployeeLeaves() {
        return ResponseEntity.ok(employeeLeaveService.getAllEmployeeLeavesAsDto());
    }

    @GetMapping("/applied-leaves/date-range")
    public ResponseEntity<List<EmployeeLeaveDto>> getEmployeeLeavesByDateRange(
            @RequestParam String fromDate,
            @RequestParam String toDate) {
        return ResponseEntity.ok(employeeLeaveService.getEmployeeLeavesByDateRangeAsDto(fromDate, toDate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeave(@PathVariable Long id) {
        employeeLeaveService.deleteEmployeeLeave(id);
        return ResponseEntity.ok().build();
    }
}
