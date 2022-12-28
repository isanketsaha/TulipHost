package com.tulip.host.repository;

import com.tulip.host.data.EmployeeBasicDTO;
import com.tulip.host.data.EmployeeDetailsDTO;
import com.tulip.host.domain.Employee;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    public Employee add(Employee employee);

    public List<EmployeeBasicDTO> fetchAll();

    List<EmployeeBasicDTO> fetchAll(boolean isActive);

    public EmployeeDetailsDTO edit();

    public List<EmployeeBasicDTO> searchByName(String name);

    public EmployeeDetailsDTO search(long id);
}
