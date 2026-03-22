package com.tulip.host.web.rest;

import com.tulip.host.data.InventoryRequestDTO;
import com.tulip.host.enums.InventoryRequestEvents;
import com.tulip.host.enums.InventoryRequestStatus;
import com.tulip.host.service.InventoryRequestService;
import com.tulip.host.web.rest.vm.InventoryRequestActionVM;
import com.tulip.host.web.rest.vm.SubmitInventoryRequestVM;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/inventory-request")
@RequiredArgsConstructor
public class InventoryRequestController {

    private final InventoryRequestService inventoryRequestService;

    @Autowired
    @Qualifier("inventorySmService")
    private StateMachineService<InventoryRequestStatus, InventoryRequestEvents> inventorySmService;

    @PostMapping("/submit")
    public ResponseEntity<Void> submit(@Valid @RequestBody SubmitInventoryRequestVM vm) {
        inventoryRequestService.submit(vm);
        return ResponseEntity.ok().build();
    }

    /**
     * GET /inventory-request/my-requests
     * Returns all requests submitted by the current user (all statuses).
     */
    @GetMapping("/my-requests")
    public ResponseEntity<List<InventoryRequestDTO>> getMyRequests() {
        return ResponseEntity.ok(inventoryRequestService.getMyRequests());
    }

    /**
     * GET /inventory-request/queue
     * Returns only requests that have a PENDING ActionNotification for the
     * current user's role.  This is the approval queue for PRINCIPAL / ADMIN.
     */
    @GetMapping("/queue")
    @PreAuthorize("hasAuthority('UG_PRINCIPAL') or hasAuthority('UG_ADMIN')")
    public ResponseEntity<List<InventoryRequestDTO>> getApprovalQueue() {
        return ResponseEntity.ok(inventoryRequestService.getApprovalQueue());
    }

    /**
     * POST /inventory-request/action
     * APPROVE, REJECT, or FULFILL a request.
     * APPROVE and REJECT require PRINCIPAL/ADMIN.  FULFILL also requires PRINCIPAL/ADMIN
     * since only office staff physically hand over the items.
     */
    @PostMapping("/action")
    @PreAuthorize("hasAuthority('UG_PRINCIPAL') or hasAuthority('UG_ADMIN')")
    public ResponseEntity<Void> processAction(@Valid @RequestBody InventoryRequestActionVM actionVM) {
        StateMachine<InventoryRequestStatus, InventoryRequestEvents> sm = inventorySmService.acquireStateMachine(actionVM.getMachineId());

        sm
            .sendEvent(
                Mono.just(
                    MessageBuilder.withPayload(actionVM.getAction())
                        .setHeader("requestId", actionVM.getRequestId())
                        .setHeader("remarks", actionVM.getRemarks())
                        .build()
                )
            )
            .subscribe();

        inventorySmService.releaseStateMachine(actionVM.getMachineId());
        return ResponseEntity.ok().build();
    }

    /**
     * DELETE /inventory-request/{id}
     * Requester can withdraw their own PENDING request.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        inventoryRequestService.delete(id);
        return ResponseEntity.ok().build();
    }
}
