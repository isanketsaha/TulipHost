package com.tulip.host.web.rest;

import com.tulip.host.domain.EmployeeLeave;
import com.tulip.host.domain.LeaveType;
import com.tulip.host.service.EmployeeLeaveService;
import com.tulip.host.service.LeaveTypeService;
import com.tulip.host.web.rest.vm.ApplyLeaveVM;
import com.tulip.host.data.LeaveBalanceDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        Optional<LeaveType> leaveType = leaveTypeService.getLeaveTypeById(id);
        return leaveType.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/balance/{employeeId}")
    public ResponseEntity<List<LeaveBalanceDTO>> getBalanceByEmpId(@PathVariable String employeeId) {
        return ResponseEntity.ok(employeeLeaveService.getBalanceByEmpId(employeeId));
    }

    @GetMapping("/appliedLeave/{employeeId}")
    public ResponseEntity<List<LeaveBalanceDTO>> getAppliedLeaveByEmpId(@PathVariable String employeeId) {
        return ResponseEntity.ok(employeeLeaveService.getBalanceByEmpId(employeeId));
    }

    @GetMapping("/types")
    public ResponseEntity<List<LeaveType>> getAllLeaveTypes() {
        return ResponseEntity.ok(leaveTypeService.getAllLeaveTypes());
    }

    @PutMapping("/types/{id}")
    public ResponseEntity<LeaveType> updateLeaveType(@PathVariable Long id, @RequestBody LeaveType leaveType) {
        return ResponseEntity.ok(leaveTypeService.updateLeaveType(id, leaveType));
    }

    @DeleteMapping("/types/{id}")
    public ResponseEntity<Void> deleteLeaveType(@PathVariable Long id) {
        leaveTypeService.deleteLeaveType(id);
        return ResponseEntity.noContent().build();
    }

    // EmployeeLeave Endpoints
    @PostMapping("/apply-leaves")
    public ResponseEntity<EmployeeLeave> createEmployeeLeave(@Valid @RequestBody ApplyLeaveVM applyLeaveVM) {
        return ResponseEntity.ok(employeeLeaveService.createEmployeeLeaveFromVM(applyLeaveVM));
    }

    @GetMapping("/applied-leaves/{employeeId}")
    public ResponseEntity<List<EmployeeLeave>> getEmployeeLeaveByEmployeeId(@PathVariable String employeeId) {
        List<EmployeeLeave> employeeLeaves = employeeLeaveService.getEmployeeLeaveByEmployeeId(employeeId);
        return ResponseEntity.ok(employeeLeaves);
    }

    @GetMapping("/applied-leaves")
    public ResponseEntity<List<EmployeeLeave>> getAllEmployeeLeaves() {
        return ResponseEntity.ok(employeeLeaveService.getAllEmployeeLeaves());
    }

    @PutMapping("/applied-leaves/{id}")
    public ResponseEntity<EmployeeLeave> updateEmployeeLeave(@PathVariable Long id,
            @RequestBody EmployeeLeave employeeLeave) {
        return ResponseEntity.ok(employeeLeaveService.updateEmployeeLeave(id, employeeLeave));
    }

    @DeleteMapping("/applied-leaves/{id}")
    public ResponseEntity<Void> deleteEmployeeLeave(@PathVariable Long id) {
        employeeLeaveService.deleteEmployeeLeave(id);
        return ResponseEntity.noContent().build();
    }
}
