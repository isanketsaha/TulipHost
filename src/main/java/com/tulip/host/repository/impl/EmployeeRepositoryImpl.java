package com.tulip.host.repository.impl;

import com.tulip.host.domain.Credential;
import com.tulip.host.domain.Employee;
import com.tulip.host.repository.EmployeeRepository;
import javax.persistence.EntityManager;

public class EmployeeRepositoryImpl extends BaseRepositoryImpl<Employee, Long> implements EmployeeRepository {

    protected EmployeeRepositoryImpl(EntityManager em) {
        super(Employee.class, em);
    }

    @Override
    public void addEmployee(Employee employee) {}
}
