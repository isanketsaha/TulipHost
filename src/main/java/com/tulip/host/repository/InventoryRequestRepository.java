package com.tulip.host.repository;

import com.tulip.host.domain.InventoryRequest;
import com.tulip.host.enums.InventoryRequestStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRequestRepository extends JpaRepository<InventoryRequest, Long> {
    // All requests submitted by a specific employee (requester's view — all statuses)
    List<InventoryRequest> findByEmployeeIdOrderByCreatedDateDesc(Long employeeId);

    // Used to locate the request during state machine actions via machineId
    java.util.Optional<InventoryRequest> findByMachineId(String machineId);

    // Filter by status, e.g. for reporting
    List<InventoryRequest> findByStatusOrderByCreatedDateDesc(InventoryRequestStatus status);
}
