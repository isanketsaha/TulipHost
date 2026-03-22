package com.tulip.host.state;

import static com.tulip.host.security.SecurityUtils.hasCurrentUserAnyOfAuthorities;

import com.tulip.host.enums.InventoryRequestEvents;
import com.tulip.host.enums.InventoryRequestStatus;
import com.tulip.host.enums.UserRoleEnum;
import com.tulip.host.service.ActionNotificationService;
import com.tulip.host.service.InventoryRequestService;
import com.tulip.host.service.StateAuditService;
import java.util.EnumSet;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.statemachine.state.State;

@Configuration
@EnableStateMachineFactory(name = "inventoryRequestStateMachineFactory", contextEvents = false)
@Slf4j
public class InventoryRequestStateConfig extends StateMachineConfigurerAdapter<InventoryRequestStatus, InventoryRequestEvents> {

    @Autowired
    @Qualifier("inventorySmPersister")
    private StateMachineRuntimePersister<InventoryRequestStatus, InventoryRequestEvents, String> persister;

    @Autowired
    private InventoryRequestService inventoryRequestService;

    @Autowired
    private ActionNotificationService actionNotificationService;

    @Autowired
    private StateAuditService stateAuditService;

    // ── Configuration ────────────────────────────────────────────────────────

    @Override
    public void configure(StateMachineConfigurationConfigurer<InventoryRequestStatus, InventoryRequestEvents> config) throws Exception {
        config.withConfiguration().listener(auditListener()).and().withPersistence().runtimePersister(persister);
    }

    @Override
    public void configure(StateMachineStateConfigurer<InventoryRequestStatus, InventoryRequestEvents> states) throws Exception {
        states.withStates().initial(InventoryRequestStatus.PENDING).states(EnumSet.allOf(InventoryRequestStatus.class));
    }

    /**
     * Single-level approval (covers 95% of cases):
     *
     *   PENDING ──(APPROVE)──► APPROVED ──(FULFILL)──► FULFILLED
     *   PENDING ──(REJECT)───► REJECTED
     *
     * Routing (who gets the ActionNotification) is decided at submit time in
     * InventoryRequestService.submit() via InventoryApprovalRouter:
     *   - TEACHER / STAFF  → notifies PRINCIPAL
     *   - PRINCIPAL        → notifies ADMIN
     *
     * To add two-level approval later:
     *   1. Add PENDING → PRINCIPAL_APPROVED transition (guard: isPrincipal)
     *   2. Add PRINCIPAL_APPROVED → APPROVED transition (guard: isAdmin)
     *   3. In decisionAction, when target == PRINCIPAL_APPROVED, chain a new
     *      ActionNotification for ADMIN before returning.
     *   No other structural change needed.
     */
    @Override
    public void configure(StateMachineTransitionConfigurer<InventoryRequestStatus, InventoryRequestEvents> transitions) throws Exception {
        transitions
            .withHistory()
            .and()
            .withExternal()
            .source(InventoryRequestStatus.PENDING)
            .target(InventoryRequestStatus.APPROVED)
            .event(InventoryRequestEvents.APPROVE)
            .guard(isAuthorized())
            .action(decisionAction())
            .and()
            .withExternal()
            .source(InventoryRequestStatus.PENDING)
            .target(InventoryRequestStatus.REJECTED)
            .event(InventoryRequestEvents.REJECT)
            .guard(isAuthorized())
            .action(decisionAction())
            .and()
            .withExternal()
            .source(InventoryRequestStatus.APPROVED)
            .target(InventoryRequestStatus.FULFILLED)
            .event(InventoryRequestEvents.FULFILL)
            .guard(isAuthorized())
            .action(fulfillAction());
    }

    // ── Guards ───────────────────────────────────────────────────────────────

    private Guard<InventoryRequestStatus, InventoryRequestEvents> isAuthorized() {
        return context -> hasCurrentUserAnyOfAuthorities(UserRoleEnum.ADMIN.getValue(), UserRoleEnum.PRINCIPAL.getValue());
    }

    // ── Actions ──────────────────────────────────────────────────────────────

    /**
     * Handles APPROVE and REJECT transitions.
     * Updates the request status to the state machine's target state and
     * closes the current ActionNotification.
     *
     * Extension point: to chain a second-level notification (e.g. to ADMIN after
     * PRINCIPAL approves), check context.getTarget().getId() here and call
     * actionNotificationService.create(...) before returning.
     */
    private Action<InventoryRequestStatus, InventoryRequestEvents> decisionAction() {
        return context -> {
            try {
                Long requestId = context.getMessageHeaders().get("requestId", Long.class);
                String remarks = context.getMessageHeaders().get("remarks", String.class);
                InventoryRequestStatus target = context.getTarget().getId();

                inventoryRequestService.updateStatus(requestId, target, remarks);
                actionNotificationService.markAction(context.getStateMachine().getId());
                inventoryRequestService.sendDecisionEmail(requestId, target, remarks);

                // When approved, raise a new notification so ADMIN sees it in the
                // fulfill queue — same mechanism as the initial submit notification.
                if (target == InventoryRequestStatus.APPROVED) {
                    String requesterName = inventoryRequestService.findById(requestId).getEmployee().getName();
                    actionNotificationService.create(
                        "INVENTORY_REQUEST",
                        requestId,
                        context.getStateMachine().getId(),
                        InventoryRequestStatus.APPROVED.name(),
                        InventoryRequestEvents.FULFILL.name(),
                        UserRoleEnum.ADMIN,
                        null,
                        requesterName
                    );
                }

                log.info("InventoryRequest {} — requestId={}", target.name(), requestId);
            } catch (Exception e) {
                log.error("Error during inventory request decision: {}", e.getMessage(), e);
                throw e;
            }
        };
    }

    private Action<InventoryRequestStatus, InventoryRequestEvents> fulfillAction() {
        return context -> {
            try {
                Long requestId = context.getMessageHeaders().get("requestId", Long.class);
                String remarks = context.getMessageHeaders().get("remarks", String.class);
                inventoryRequestService.updateStatus(requestId, InventoryRequestStatus.FULFILLED, remarks);
                actionNotificationService.markAction(context.getStateMachine().getId());
                inventoryRequestService.sendFulfillEmail(requestId);
                log.info("InventoryRequest FULFILLED — requestId={}", requestId);
            } catch (Exception e) {
                log.error("Error during inventory request fulfillment: {}", e.getMessage(), e);
                throw e;
            }
        };
    }

    // ── Audit listener ───────────────────────────────────────────────────────

    private StateMachineListener<InventoryRequestStatus, InventoryRequestEvents> auditListener() {
        return new StateMachineListenerAdapter<>() {
            @Override
            public void stateContext(StateContext<InventoryRequestStatus, InventoryRequestEvents> ctx) {
                String machineId = ctx.getStateMachine().getId();
                if (ctx.getStage() == StateContext.Stage.EVENT_NOT_ACCEPTED) {
                    log.error("InventoryRequest event not accepted: {}", ctx);
                    stateAuditService.recordFailedTransition(
                        machineId,
                        ofNullable(ctx.getSource()),
                        ctx.getEvent().name(),
                        "Event not accepted by state machine"
                    );
                }
                if (ctx.getStage() == StateContext.Stage.STATE_CHANGED) {
                    stateAuditService.recordTransition(
                        machineId,
                        ofNullable(ctx.getSource()),
                        ofNullable(ctx.getTarget()),
                        ctx.getEvent() != null ? ctx.getEvent().name() : "",
                        true,
                        "Event accepted by state machine"
                    );
                }
            }

            private String ofNullable(State<InventoryRequestStatus, InventoryRequestEvents> s) {
                return Optional.ofNullable(s).map(State::getId).map(Enum::name).orElse(null);
            }
        };
    }
}
