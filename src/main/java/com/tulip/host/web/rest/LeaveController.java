package com.tulip.host.web.rest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tulip.host.data.EmployeeLeaveDto;
import com.tulip.host.data.LeaveBalanceDTO;
import com.tulip.host.domain.EmployeeLeave;
import com.tulip.host.enums.LeaveEvents;
import com.tulip.host.enums.LeaveStatus;
import com.tulip.host.service.EmployeeLeaveService;
import com.tulip.host.web.rest.vm.ApplyLeaveVM;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/leave")
@RequiredArgsConstructor
public class LeaveController {

    private final EmployeeLeaveService employeeLeaveService;

    private final StateMachineService<LeaveStatus, LeaveEvents> stateMachineService;

    @GetMapping("/balance")
    @PreAuthorize("hasAuthority('UG_PRINCIPAL') or hasAuthority('UG_ADMIN')")
    public ResponseEntity<Map<String, List<LeaveBalanceDTO>>> getBalance() {
        return ResponseEntity.ok(employeeLeaveService.getBalance());
    }

    @GetMapping("/balance/{employeeId}")
    public ResponseEntity<List<LeaveBalanceDTO>> getBalanceByEmpId(@PathVariable Long employeeId) {
        return ResponseEntity.ok(employeeLeaveService.getBalanceByEmpId(employeeId));
    }

    @GetMapping("/balance/tid/{tid}")
    @PreAuthorize("hasAuthority('UG_PRINCIPAL') or hasAuthority('UG_ADMIN')")
    public ResponseEntity<List<LeaveBalanceDTO>> getBalanceByTid(@PathVariable String tid) {
        return ResponseEntity.ok(employeeLeaveService.getBalanceByTid(tid));
    }

    @GetMapping("/appliedLeave/{employeeId}")
    public ResponseEntity<List<EmployeeLeave>> getAppliedLeave(@PathVariable Long employeeId) {
        return ResponseEntity.ok(employeeLeaveService.getAppliedLeaveByEmpId(employeeId));
    }

    @PostMapping("/apply")
    public ResponseEntity<Void> applyLeave(@Valid @RequestBody ApplyLeaveVM applyLeaveVM) {
        String id = UUID.randomUUID().toString();
        StateMachine<LeaveStatus, LeaveEvents> stateMachine = stateMachineService.acquireStateMachine(id);
        stateMachine.sendEvent(Mono.just(MessageBuilder
                .withPayload(LeaveEvents.SUBMIT)
                .setHeader("leaveVm", applyLeaveVM)
                .build())).subscribe();
        stateMachineService.releaseStateMachine(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/applied-leaves/{employeeId}")
    public ResponseEntity<List<EmployeeLeaveDto>> getEmployeeLeaveByEmployeeId(@PathVariable String employeeId) {
        if ("ALL".equals(employeeId)) {
            List<EmployeeLeaveDto> allEmployeeLeaves = employeeLeaveService.getAllEmployeeLeavesAsDto(null);
            return ResponseEntity.ok(allEmployeeLeaves);
        } else {
            List<EmployeeLeaveDto> employeeLeaves = employeeLeaveService
                    .getEmployeeLeaveByEmployeeIdAsDto(Long.parseLong(employeeId));
            return ResponseEntity.ok(employeeLeaves);
        }
    }

    @PostMapping("/action")
    @PreAuthorize("hasAuthority('UG_PRINCIPAL') or hasAuthority('UG_ADMIN')")
    public ResponseEntity<Void> processLeaveAction(
            @Valid @RequestBody com.tulip.host.web.rest.vm.LeaveActionVM actionVM) {
        StateMachine<LeaveStatus, LeaveEvents> stateMachine = stateMachineService
                .acquireStateMachine(actionVM.getMachineId());
        stateMachine.sendEvent(Mono.just(MessageBuilder
                .withPayload(actionVM.getAction())
                .setHeader("leaveId", actionVM.getLeaveId())
                .setHeader("comments", actionVM.getComments())
                .build())).subscribe();
        stateMachineService.releaseStateMachine(actionVM.getMachineId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/applied-leaves")
    public ResponseEntity<List<EmployeeLeaveDto>> getAllEmployeeLeaves(
            @RequestParam(required = false) LeaveStatus status) {
        return ResponseEntity.ok(employeeLeaveService.getAllEmployeeLeavesAsDto(status));
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
        return ResponseEntity.ok()
                .build();
    }
}
