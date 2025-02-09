package com.tulip.host.workflow;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.region.RegionExecutionPolicy;
import org.springframework.statemachine.transition.TransitionConflictPolicy;

import java.util.Arrays;
import java.util.HashSet;

@Configuration
@Slf4j
@EnableStateMachine(name = "ExpenseWorkflow")
public class ExpenseWorkflow extends StateMachineConfigurerAdapter<String, String> {

//    @Bean
//    public StateMachineService<String, String> stateMachineService(
//        final StateMachineFactory<String, String> stateMachineFactory,
//        final StateMachinePersist<WorkflowState, WorkflowEvent, String> stateMachinePersist) {
//        return new DefaultStateMachineService<>(stateMachineFactory, stateMachinePersist);
//    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<String, String> config)
        throws Exception {
        config
            .withConfiguration()
            .autoStartup(true)
            .machineId("myMachineId")
            .beanFactory(new StaticListableBeanFactory())
            .listener(new StateMachineListenerAdapter<String, String>())
            .transitionConflictPolicy(TransitionConflictPolicy.CHILD)
            .regionExecutionPolicy(RegionExecutionPolicy.PARALLEL);
    }

    @Override
    public void configure(StateMachineStateConfigurer<String, String> states)
        throws Exception {
        states
            .withStates()
            .initial("PENDING")
            .states(new HashSet<String>(Arrays.asList("PENDING", "REJECTED", "APPROVED")));
    }

    public void configure(StateMachineTransitionConfigurer<String, String> transitions)
        throws Exception {
        transitions
            .withExternal()
            .source("PENDING")
            .target("REJECTED")
            .event("EXPENSE_TRIGGER")
            .guard(validate())
            .source("PENDING")
            .target("APPROVED")
            .event("EXPENSE_TRIGGER")
            .action(review());
        ;

    }

    @Bean
    public Action<String, String> review() {
        return context -> {
            try {
                Long value = context.getExtendedState()
                    .get("EXPENSE_ID", Long.class);
                System.out.println("Action Sanket = " + value);
            } catch (Exception e) {
                log.error(e.getLocalizedMessage());
            }
        };
    }


    @Bean
    public Guard<String, String> validate() {
        return (context) -> {
            return true;
        };
    }

}
