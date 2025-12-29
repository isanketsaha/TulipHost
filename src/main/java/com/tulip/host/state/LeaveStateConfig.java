package com.tulip.host.state;

import static com.tulip.host.security.SecurityUtils.hasCurrentUserAnyOfAuthorities;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.tulip.host.service.EmployeeService;
import org.apache.commons.lang3.StringUtils;
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

import com.tulip.host.domain.Employee;
import com.tulip.host.domain.EmployeeLeave;
import com.tulip.host.enums.LeaveEvents;
import com.tulip.host.enums.LeaveStatus;
import com.tulip.host.enums.UserRoleEnum;
import com.tulip.host.repository.EmployeeRepository;
import com.tulip.host.service.ActionNotificationService;
import com.tulip.host.service.EmployeeLeaveService;
import com.tulip.host.service.MailService;
import com.tulip.host.service.StateAuditService;
import com.tulip.host.service.communication.CommunicationRequest;
import com.tulip.host.service.communication.OutboundCommunicationService;
import com.tulip.host.web.rest.vm.ApplyLeaveVM;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@EnableStateMachineFactory(contextEvents = false)
@Slf4j
public class LeaveStateConfig extends StateMachineConfigurerAdapter<LeaveStatus, LeaveEvents> {
    private final EmployeeRepository employeeRepository;

    private final StateMachineRuntimePersister<LeaveStatus, LeaveEvents, String> stateMachineRuntimePersister;

    private final EmployeeLeaveService employeeLeaveService;

    private final ActionNotificationService actionNotificationService;

    private final EmployeeService employeeService;

    private final StateAuditService stateAuditService;

    private final MailService mailService;

    private final OutboundCommunicationService outboundCommunicationService;


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
                            stateContext.getEvent() != null ? stateContext.getEvent()
                                            .name() : "",
                            true,
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
        transitions.withHistory()
                .and()
                .withInternal()
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
                String machineId = context.getStateMachine()
                        .getId();
                UserRoleEnum role = employee.getGroup()
                        .getAuthority()
                        .equals(UserRoleEnum.PRINCIPAL.getValue()) ? UserRoleEnum.ADMIN : UserRoleEnum.PRINCIPAL;

                if (leaveVm.getTid() != null && !leaveVm.getTid()
                        .isEmpty()) {
                    log.info("Auto-approving leave with tid: leaveId={}, tid={}", leave.getId(), leaveVm.getTid());
                    leaveVm.setStatus(LeaveStatus.APPROVED);
                    context.getStateMachine()
                            .sendEvent(
                            org.springframework.messaging.support.MessageBuilder
                                            .withPayload(LeaveEvents.APPROVE)
                                            .setHeader("leaveId", leave.getId())
                                            .setHeader("autoApproved", true)
                                            .setHeader("comments", "Auto-approved (TID provided)")
                                            .build());
                } else {
                    actionNotificationService.create("LEAVE", leave.getId(), machineId, LeaveStatus.PENDING.name(),
                                    LeaveEvents.APPROVE.name(), role, null, leave.getEmployee()
                                    .getName());
                }
                sendEmail(leave, "mail/leave-applied.vm", leaveVm.getTid() != null ? "Auto-approved" : "Submitted");
            } catch (Exception e) {
                log.error("Error during leave submission: {}", e.getMessage(), e);
                throw e;
            }
        };
    }

    private void sendEmail(EmployeeLeave leave, String templatePath, String action) {
        try {
            Employee employee = leave.getEmployee();
            Map<String, Object> model = Map.of("user", employee, "leave", leave,
                "employeeName", employee.getName(), "action", action);
            String[] ccEmails = employeeService.getCCEmails(employee.getGroup()
                .getAuthority());
            String[] to = !StringUtils.isEmpty(employee.getEmail()) ? new String[]{employee.getEmail()} : ccEmails;
            outboundCommunicationService.send(CommunicationRequest.builder()
                    .channel(com.tulip.host.enums.CommunicationChannel.EMAIL)
                    .recipient(to)
                    .cc(ccEmails)
                    .subject(employee.getName() +" - Leave applied of " + leave.getStartDate() + " to " + leave.getEndDate())
                    .content(mailService.renderTemplate(templatePath, model))
                    .entityType(leave.getClass().getName())
                    .entityId(leave.getId())
                    .build());

        } catch (Exception e) {
            log.warn("Leave submission email failed : {}", e.getMessage(), e);
        }
    }

    private Action<LeaveStatus, LeaveEvents> action() {
        return context -> {
            try {
                Long leaveId = context.getMessageHeaders()
                        .get("leaveId", Long.class);
                String comments = context.getMessageHeaders()
                        .get("comments", String.class);
                LeaveEvents event = context.getEvent();
                Boolean autoApproved = context.getMessageHeaders()
                    .get("autoApproved", Boolean.class);
                log.info("Leave {} leaveId={}", event.name(), leaveId);
                employeeLeaveService.updateStatus(leaveId, event.equals(LeaveEvents.APPROVE)
                    ? LeaveStatus.APPROVED : LeaveStatus.REJECTED, comments);
                String machineId = context.getStateMachine()
                        .getId();
                actionNotificationService.markAction(machineId);
                if(!autoApproved) {
                    EmployeeLeave byId = employeeLeaveService.findById(leaveId);
                    sendEmail(byId, "mail/leave-applied.vm", event.name());
                }
            } catch (Exception e) {
                log.error("Error during leave approval: {}", e.getMessage(), e);
                throw e;
            }
        };
    }

}
