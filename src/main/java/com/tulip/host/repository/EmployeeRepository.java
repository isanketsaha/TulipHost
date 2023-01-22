package com.tulip.host.repository;

import com.tulip.host.data.EmployeeDetailsDTO;
import com.tulip.host.domain.Employee;
import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    public List<Employee> fetchAll();

    List<Employee> fetchAll(boolean isActive);

    public EmployeeDetailsDTO edit();

    public List<Employee> searchByName(String name);

    public Employee search(long id);

    Map<String, Long> fetchStaffReport();
}
