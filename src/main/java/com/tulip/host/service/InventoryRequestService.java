package com.tulip.host.service;

import com.tulip.host.data.InventoryRequestDTO;
import com.tulip.host.domain.Employee;
import com.tulip.host.domain.InventoryRequest;
import com.tulip.host.domain.ProductCatalog;
import com.tulip.host.enums.InventoryRequestEvents;
import com.tulip.host.enums.InventoryRequestStatus;
import com.tulip.host.enums.UserRoleEnum;
import com.tulip.host.mapper.InventoryRequestMapper;
import com.tulip.host.repository.EmployeeRepository;
import com.tulip.host.repository.InventoryRequestRepository;
import com.tulip.host.repository.ProductCatalogRepository;
import com.tulip.host.security.SecurityUtils;
import com.tulip.host.utils.InventoryApprovalRouter;
import com.tulip.host.web.rest.vm.SubmitInventoryRequestVM;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class InventoryRequestService {

    private final InventoryRequestRepository inventoryRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final ProductCatalogRepository productCatalogRepository;
    private final InventoryRequestMapper inventoryRequestMapper;
    private final ActionNotificationService actionNotificationService;
    private final MailService mailService;
    private final InventoryApprovalRouter approvalRouter;

    /**
     * Directly creates and persists an InventoryRequest, then raises the
     * ActionNotification for the approver. Bypasses the state machine for
     * submission so the security context is reliably available.
     */
    public void submit(SubmitInventoryRequestVM vm) {
        String userId = SecurityUtils.getCurrentUserId().orElseThrow(() -> new RuntimeException("No authenticated user"));
        Employee employee = employeeRepository
            .findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Employee not found for userId: " + userId));
        ProductCatalog product = productCatalogRepository
            .findById(vm.getProductCatalogId())
            .orElseThrow(() -> new RuntimeException("Product not found: " + vm.getProductCatalogId()));

        String machineId = "inv-" + UUID.randomUUID();

        InventoryRequest request = InventoryRequest.builder()
            .employee(employee)
            .product(product)
            .qty(vm.getQty())
            .justification(vm.getJustification())
            .machineId(machineId)
            .status(InventoryRequestStatus.PENDING)
            .build();
        inventoryRequestRepository.save(request);

        UserRoleEnum requesterRole = UserRoleEnum.fromValue(employee.getGroup().getAuthority());
        UserRoleEnum approverRole = approvalRouter.getApproverFor(requesterRole);

        actionNotificationService.create(
            "INVENTORY_REQUEST",
            request.getId(),
            machineId,
            InventoryRequestStatus.PENDING.name(),
            InventoryRequestEvents.APPROVE.name(),
            approverRole,
            null,
            employee.getName()
        );

        sendSubmitEmails(request, approverRole);

        log.info(
            "InventoryRequest submitted: id={}, employee={}, product={}, qty={}",
            request.getId(),
            employee.getName(),
            product.getItemName(),
            vm.getQty()
        );
    }

    /**
     * Called from the state machine on APPROVE / REJECT / FULFILL actions.
     */
    public InventoryRequest updateStatus(Long requestId, InventoryRequestStatus status, String remarks) {
        InventoryRequest request = inventoryRequestRepository
            .findById(requestId)
            .orElseThrow(() -> new RuntimeException("InventoryRequest not found: " + requestId));
        request.setStatus(status);
        request.setRemarks(remarks);
        if (
            status == InventoryRequestStatus.APPROVED ||
            status == InventoryRequestStatus.REJECTED ||
            status == InventoryRequestStatus.FULFILLED
        ) {
            request.setApprovedBy(SecurityUtils.getCurrentUserLogin().orElse("system"));
            request.setApprovedDate(LocalDateTime.now());
        }
        return inventoryRequestRepository.save(request);
    }

    /**
     * Requester view: returns ALL requests submitted by the current user (all
     * statuses).
     * This is what the teacher/staff sees in their "My Requests" tab.
     */
    @Transactional(readOnly = true)
    public List<InventoryRequestDTO> getMyRequests() {
        String userId = SecurityUtils.getCurrentUserId().orElseThrow(() -> new RuntimeException("No authenticated user"));
        Employee employee = employeeRepository
            .findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Employee not found for userId: " + userId));
        return inventoryRequestRepository
            .findByEmployeeIdOrderByCreatedDateDesc(employee.getId())
            .stream()
            .map(inventoryRequestMapper::toDto)
            .collect(Collectors.toList());
    }

    /**
     * Approver view: returns only the requests that have a PENDING
     * ActionNotification
     * routed to the current user's role. A principal who submitted their own
     * request
     * will NOT see it here because that notification goes to ADMIN.
     *
     * This is the correct pattern — the source of truth for "what needs my
     * attention"
     * is always the action_notification table, not the entity status alone.
     */
    @Transactional(readOnly = true)
    public List<InventoryRequestDTO> getApprovalQueue() {
        List<Long> ids = actionNotificationService.getApprovalQueueEntityIds("INVENTORY_REQUEST");
        if (ids.isEmpty()) {
            return List.of();
        }
        return inventoryRequestRepository.findAllById(ids).stream().map(inventoryRequestMapper::toDto).collect(Collectors.toList());
    }

    public InventoryRequest findById(Long id) {
        return inventoryRequestRepository.findById(id).orElseThrow(() -> new RuntimeException("InventoryRequest not found: " + id));
    }

    // ── Email helpers ─────────────────────────────────────────────────────────

    /**
     * Sends confirmation to the requester and a pending-approval alert to all
     * employees holding the approver role.
     */
    void sendSubmitEmails(InventoryRequest request, UserRoleEnum approverRole) {
        try {
            Employee requester = request.getEmployee();
            Map<String, Object> model = buildModel(request, null);

            // Notify requester
            if (StringUtils.isNotEmpty(requester.getEmail())) {
                model.put("action", "SUBMITTED");
                String content = mailService.renderTemplate("mail/inventory_request.vm", model);
                mailService.sendEmail(
                    new String[] { requester.getEmail() },
                    new String[] {},
                    "Inventory Request Submitted — " + request.getProduct().getItemName(),
                    content,
                    false,
                    true
                );
            }

            // Notify approvers by role
            model.put("action", "APPROVAL_PENDING");
            String approverContent = mailService.renderTemplate("mail/inventory_request.vm", model);
            String[] approverEmails = employeeRepository
                .findByUserGroup(approverRole)
                .stream()
                .map(Employee::getEmail)
                .filter(StringUtils::isNotEmpty)
                .toArray(String[]::new);
            if (approverEmails.length > 0) {
                mailService.sendEmail(
                    approverEmails,
                    new String[] {},
                    "Action Required: Inventory Request from " + requester.getName(),
                    approverContent,
                    false,
                    true
                );
            }
        } catch (Exception e) {
            log.warn("Inventory request submit email failed for request {}: {}", request.getId(), e.getMessage());
        }
    }

    /**
     * Sends APPROVED / REJECTED outcome email to the requester.
     */
    public void sendDecisionEmail(Long requestId, InventoryRequestStatus status, String remarks) {
        try {
            InventoryRequest request = findById(requestId);
            Employee requester = request.getEmployee();
            if (StringUtils.isEmpty(requester.getEmail())) return;

            Map<String, Object> model = buildModel(request, remarks);
            model.put("action", status.name());
            String content = mailService.renderTemplate("mail/inventory_request.vm", model);
            mailService.sendEmail(
                new String[] { requester.getEmail() },
                new String[] {},
                "Inventory Request " + status.name() + " — " + request.getProduct().getItemName(),
                content,
                false,
                true
            );
        } catch (Exception e) {
            log.warn("Inventory request decision email failed for request {}: {}", requestId, e.getMessage());
        }
    }

    /**
     * Sends FULFILLED notification to the requester.
     */
    public void sendFulfillEmail(Long requestId) {
        try {
            InventoryRequest request = findById(requestId);
            Employee requester = request.getEmployee();
            if (StringUtils.isEmpty(requester.getEmail())) return;

            Map<String, Object> model = buildModel(request, null);
            model.put("action", "FULFILLED");
            String content = mailService.renderTemplate("mail/inventory_request.vm", model);
            mailService.sendEmail(
                new String[] { requester.getEmail() },
                new String[] {},
                "Inventory Request Fulfilled — " + request.getProduct().getItemName(),
                content,
                false,
                true
            );
        } catch (Exception e) {
            log.warn("Inventory request fulfill email failed for request {}: {}", requestId, e.getMessage());
        }
    }

    private Map<String, Object> buildModel(InventoryRequest request, String remarks) {
        Map<String, Object> model = new java.util.HashMap<>();
        model.put("requesterName", request.getEmployee().getName());
        model.put("productName", request.getProduct().getItemName());
        model.put("qty", request.getQty());
        model.put("justification", request.getJustification());
        model.put("status", request.getStatus().name());
        model.put("remarks", remarks);
        return model;
    }

    /**
     * Delete a PENDING request and clean up its ActionNotification.
     */
    public void delete(Long id) {
        InventoryRequest request = findById(id);
        if (request.getStatus() != InventoryRequestStatus.PENDING) {
            throw new RuntimeException("Only PENDING requests can be deleted");
        }
        actionNotificationService.deleteByEntityTypeAndEntityId("INVENTORY_REQUEST", id);
        inventoryRequestRepository.deleteById(id);
        log.info("InventoryRequest deleted: id={}", id);
    }
}
