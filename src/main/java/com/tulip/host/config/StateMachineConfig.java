package com.tulip.host.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineModelConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.config.model.StateMachineModelFactory;
import org.springframework.statemachine.data.RepositoryState;
import org.springframework.statemachine.data.RepositoryStateMachineModelFactory;
import org.springframework.statemachine.data.RepositoryTransition;
import org.springframework.statemachine.data.StateRepository;
import org.springframework.statemachine.data.TransitionRepository;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.statemachine.service.DefaultStateMachineService;
import org.springframework.statemachine.service.StateMachineService;

import java.util.Set;

@Configuration
@EnableStateMachineFactory
//@EnableStateMachine
@AllArgsConstructor
public class StateMachineConfig extends StateMachineConfigurerAdapter<String, String> {

    private final StateMachineListener stateMachineListener;
    private final StateRepository<? extends RepositoryState> stateRepository;
    private final TransitionRepository<? extends RepositoryTransition> transitionRepository;

    private final StateMachineRuntimePersister<String, String, String> stateMachineRuntimePersister;


//    @Override
//    public void configure(StateMachineModelConfigurer<String, String> model) throws Exception {
//        model
//            .withModel()
//            .factory(modelFactory());
//    }
//
//    @Bean
//    public StateMachineModelFactory<String, String> modelFactory() {
//        return new RepositoryStateMachineModelFactory(stateRepository, transitionRepository);
//    }
//
//    @Bean
//    public StateMachineService<String, String> stateMachineService(
//        StateMachineFactory<String, String> stateMachineFactory,
//        StateMachineRuntimePersister<String, String, String> stateMachineRuntimePersister) {
//        return new DefaultStateMachineService<>(stateMachineFactory, stateMachineRuntimePersister);
//    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<String, String> config) throws Exception {
        config.withPersistence()
            .runtimePersister(stateMachineRuntimePersister)
            .and()
            .withConfiguration()
            .autoStartup(true)
            .listener(stateMachineListener);
    }


    // Optionally remove these if fully relying on database configuration
    @Override
    public void configure(StateMachineStateConfigurer<String, String> states) throws Exception {
        states
            .withStates()
            .initial("SUBMITTED")
            .state("UNDER_REVIEW")
            .state("APPROVED")
            .state("REJECTED").states(Set.of("SUBMITTED", "UNDER_REVIEW", "APPROVED", "REJECTED"));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<String, String> transitions) throws Exception {
        transitions
            .withExternal()
            .source("SUBMITTED").target("UNDER_REVIEW").event("REVIEW")
            .and()
            .withExternal()
            .source("UNDER_REVIEW").target("APPROVED").event("APPROVE")
            .and()
            .withExternal()
            .source("UNDER_REVIEW").target("REJECTED").event("REJECT");
    }
}
