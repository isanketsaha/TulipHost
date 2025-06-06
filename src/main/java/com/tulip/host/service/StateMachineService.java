package com.tulip.host.service;

import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Service;

@Service
public class StateMachineService {

    private final StateMachineFactory<String, String> stateMachineFactory;

    public StateMachineService(StateMachineFactory<String, String> stateMachineFactory) {
        this.stateMachineFactory = stateMachineFactory;
    }

    public StateMachine<String, String> getStateMachine(String machineId) {
        StateMachine<String, String> stateMachine = stateMachineFactory.getStateMachine(machineId);
        stateMachine.start();
        return stateMachine;
    }

    public void sendEvent(String machineId, String event) {
        StateMachine<String, String> stateMachine = getStateMachine(machineId);
        stateMachine.sendEvent(event);
    }
}
