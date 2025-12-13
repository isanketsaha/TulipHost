package com.tulip.host.state;

import com.tulip.host.enums.LeaveEvents;
import com.tulip.host.enums.LeaveStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.data.jpa.JpaPersistingStateMachineInterceptor;
import org.springframework.statemachine.data.jpa.JpaStateMachineRepository;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.statemachine.service.DefaultStateMachineService;
import org.springframework.statemachine.service.StateMachineService;

@Configuration
public class PersistenceConfig {


    @Bean
    public StateMachineRuntimePersister<LeaveStatus, LeaveEvents, String> stateMachineRuntimePersister(
        JpaStateMachineRepository jpaRepo) {
        return new JpaPersistingStateMachineInterceptor<>(jpaRepo);
    }

    @Bean
    public StateMachineService<LeaveStatus, LeaveEvents> stateMachineService(StateMachineFactory<LeaveStatus, LeaveEvents> factory,
        StateMachineRuntimePersister<LeaveStatus, LeaveEvents, String> persister) {
        return new DefaultStateMachineService<>(factory, persister);
    }

}
