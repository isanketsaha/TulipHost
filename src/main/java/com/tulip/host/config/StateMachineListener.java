package com.tulip.host.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

@Configuration
@Slf4j
public class StateMachineListener extends StateMachineListenerAdapter {

    @Override
    public void stateChanged(State from, State to) {
        log.info("Transitioned from %s to %s%n", from == null ?
            "none" : from.getId(), to.getId());
    }

}

