package com.tulip.host.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tulip.host.domain.StateAuditHistory;

@Repository
public interface StateAuditHistoryRepository extends JpaRepository<StateAuditHistory, Long> {

    List<StateAuditHistory> findByMachineIdOrderByCreatedDateDesc(String machineId);

    List<StateAuditHistory> findBySuccessFalseOrderByCreatedDateDesc();
}
