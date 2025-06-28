package com.tulip.host.repository;

import com.tulip.host.data.EmployeeDetailsDTO;
import com.tulip.host.domain.Employee;
import com.tulip.host.enums.UserRoleEnum;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    public List<Employee> fetchAll();

    List<Employee> fetchAll(boolean isActive, List<UserRoleEnum> role);

    public EmployeeDetailsDTO edit();

    public List<Employee> searchByName(String name);

    public Employee search(long id);

    Map<String, Long> fetchStaffReport();

    Optional<Employee> findByUserId(String userId);
}
