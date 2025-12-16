package com.tulip.host.state;

import java.util.EnumSet;
import java.util.Optional;

import com.tulip.host.domain.Employee;
import com.tulip.host.web.rest.vm.LeaveActionVM;
import jakarta.transaction.Transactional;
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

import com.tulip.host.enums.LeaveEvents;
import com.tulip.host.enums.LeaveStatus;
import com.tulip.host.enums.UserRoleEnum;
import com.tulip.host.service.ActionNotificationService;
import com.tulip.host.service.EmployeeLeaveService;
import com.tulip.host.service.StateAuditService;
import com.tulip.host.web.rest.vm.ApplyLeaveVM;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.tulip.host.security.SecurityUtils.hasCurrentUserAnyOfAuthorities;

@Configuration
@RequiredArgsConstructor
@EnableStateMachineFactory(contextEvents = false)
@Slf4j
public class LeaveStateConfig extends StateMachineConfigurerAdapter<LeaveStatus, LeaveEvents> {

    private final StateMachineRuntimePersister<LeaveStatus, LeaveEvents, String> stateMachineRuntimePersister;

    private final EmployeeLeaveService employeeLeaveService;

    private final ActionNotificationService actionNotificationService;

    private final StateAuditService stateAuditService;



    @Override
    public void configure(StateMachineConfigurationConfigurer<LeaveStatus, LeaveEvents> config) throws Exception {
        config
                .withConfiguration()
                .listener(listener())
                .and()
                .withPersistence()
                .runtimePersister(stateMachineRuntimePersister);
    }

    private StateMachineListener<LeaveStatus, LeaveEvents> listener() {

        return new StateMachineListenerAdapter<LeaveStatus, LeaveEvents>() {

            @Override
            public void stateContext(StateContext<LeaveStatus, LeaveEvents> stateContext) {
                String machineId = stateContext.getStateMachine()
                        .getId();
                if (stateContext.getStage()
                        .equals(StateContext.Stage.EVENT_NOT_ACCEPTED)) {
                    log.error("Event not accepted in stateContext: {}", stateContext);
                    stateAuditService.recordFailedTransition(machineId, ofNullableState(stateContext.getSource()),
                            stateContext.getEvent()
                                    .name(),
                            "Event  not accepted by state machine");
                }
                if (stateContext.getStage()
                        .equals(StateContext.Stage.STATE_CHANGED)) {
                    stateAuditService.recordTransition(machineId, ofNullableState(stateContext.getSource()),
                            ofNullableState(stateContext.getTarget()),
                            stateContext.getEvent() != null ? stateContext.getEvent().name() : "", true,
                            "Event accepted by state machine");
                }
            }

            private String ofNullableState(State<LeaveStatus, LeaveEvents> s) {
                return Optional.ofNullable(s)
                        .map(State::getId)
                        .map(Enum::name)
                        .orElse(null);
            }
        };
    }

    @Override
    public void configure(StateMachineStateConfigurer<LeaveStatus, LeaveEvents> states) throws Exception {
        states
                .withStates()
                .initial(LeaveStatus.PENDING)
                .states(EnumSet.allOf(LeaveStatus.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<LeaveStatus, LeaveEvents> transitions) throws Exception {
        transitions.withHistory().and().withInternal()
                .source(LeaveStatus.PENDING)
                .event(LeaveEvents.SUBMIT)
                .action(create())
                .and()
                .withExternal()
                .source(LeaveStatus.PENDING)
                .target(LeaveStatus.APPROVED)
                .guard(isValid())
                .event(LeaveEvents.APPROVE)
                .action(action())
                .and()
                .withExternal()
                .source(LeaveStatus.PENDING)
                .target(LeaveStatus.REJECTED)
                .guard(isValid())
                .event(LeaveEvents.REJECT)
                .action(action());
    }

    private Guard<LeaveStatus, LeaveEvents> isValid() {
        return context ->
             hasCurrentUserAnyOfAuthorities(UserRoleEnum.ADMIN.getValue(), UserRoleEnum.PRINCIPAL.getValue());

    }

    private Action<LeaveStatus, LeaveEvents> create() {
        return context -> {
            try {

                ApplyLeaveVM leaveVm = context.getMessageHeaders()
                        .get("leaveVm", ApplyLeaveVM.class);
                if (leaveVm == null) {
                    throw new IllegalStateException("leaveVm in submitAction");
                }
                leaveVm.setStatus(LeaveStatus.PENDING);
                Employee employee = employeeLeaveService.getEmployee(leaveVm);
                var leave = employeeLeaveService.createEmployeeLeaveFromVM(leaveVm);
                String machineId = context.getStateMachine().getId();

                if (leaveVm.getTid() != null && !leaveVm.getTid().isEmpty()) {
                    log.info("Auto-approving leave with tid: leaveId={}, tid={}", leave.getId(), leaveVm.getTid());
                    leaveVm.setStatus(LeaveStatus.APPROVED);
                    context.getStateMachine().sendEvent(
                            org.springframework.messaging.support.MessageBuilder
                                    .withPayload(LeaveEvents.APPROVE)
                                    .setHeader("leaveId", leave.getId())
                                    .setHeader("comments", "Auto-approved (TID provided)")
                                    .build());
                } else {
                   UserRoleEnum role =  employee.getGroup().getAuthority().equals(UserRoleEnum.PRINCIPAL.getValue()) ? UserRoleEnum.ADMIN : UserRoleEnum.PRINCIPAL;
                    actionNotificationService.create("LEAVE", leave.getId(), machineId, LeaveStatus.PENDING.name(),
                            LeaveEvents.APPROVE.name(), UserRoleEnum.ADMIN  , null, leave.getEmployee().getName());
                }
            } catch (Exception e) {
                log.error("Error during leave submission: {}", e.getMessage(), e);
                throw e;
            }
        };
    }

    private Action<LeaveStatus, LeaveEvents> action() {
        return context -> {
            try {
                Long leaveId = context.getMessageHeaders()
                        .get("leaveId", Long.class);
                String comments = context.getMessageHeaders()
                        .get("comments", String.class);
                LeaveEvents event = context.getEvent();
                log.info("Leave {} leaveId={}", event.name(), leaveId);
                employeeLeaveService.updateStatus(leaveId, event.equals(LeaveEvents.APPROVE)
                    ? LeaveStatus.APPROVED : LeaveStatus.REJECTED, comments);
                String machineId = context.getStateMachine().getId();
                actionNotificationService.markAction(machineId);
            } catch (Exception e) {
                log.error("Error during leave approval: {}", e.getMessage(), e);
                throw e;
            }
        };
    }

}
