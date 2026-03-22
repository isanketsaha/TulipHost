package com.tulip.host.state;

import com.tulip.host.enums.InventoryRequestEvents;
import com.tulip.host.enums.InventoryRequestStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.data.jpa.JpaPersistingStateMachineInterceptor;
import org.springframework.statemachine.data.jpa.JpaStateMachineRepository;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.statemachine.service.DefaultStateMachineService;
import org.springframework.statemachine.service.StateMachineService;

/**
 * Separate persistence beans for the inventory-request state machine.
 * Both machines share the same JPA tables (spring_state_machine / spring_state_machine_context)
 * but use different machineId namespaces (prefix "inv-"), so there is no data collision.
 */
@Configuration
public class InventoryPersistenceConfig {

    @Bean("inventorySmPersister")
    public StateMachineRuntimePersister<InventoryRequestStatus, InventoryRequestEvents, String> inventorySmPersister(
        JpaStateMachineRepository jpaRepo
    ) {
        return new JpaPersistingStateMachineInterceptor<>(jpaRepo);
    }

    @Bean("inventorySmService")
    public StateMachineService<InventoryRequestStatus, InventoryRequestEvents> inventorySmService(
        @Qualifier("inventoryRequestStateMachineFactory") StateMachineFactory<InventoryRequestStatus, InventoryRequestEvents> factory,
        @Qualifier("inventorySmPersister") StateMachineRuntimePersister<InventoryRequestStatus, InventoryRequestEvents, String> persister
    ) {
        return new DefaultStateMachineService<>(factory, persister);
    }
}
