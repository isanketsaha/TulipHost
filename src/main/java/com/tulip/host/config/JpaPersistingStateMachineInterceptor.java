//package com.tulip.host.config;
//
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.statemachine.data.StateMachineRepository;
//import org.springframework.statemachine.persist.StateMachineRuntimePersister;
//import org.springframework.stereotype.Component;
//
//@Component
//public class JpaPersistingStateMachineInterceptor
//    extends JpaPersistingStateMachineInterceptor<String, String, String> {
//
//    @Autowired
//    public JpaPersistingStateMachineInterceptor(StateMachineRepository repository) {
//        super(repository);
//    }
//}
//
//@Bean
//public StateMachineRuntimePersister<String, String, String> stateMachineRuntimePersister(
//    JpaPersistingStateMachineInterceptor jpaPersistingStateMachineInterceptor) {
//    return jpaPersistingStateMachineInterceptor;
//}
