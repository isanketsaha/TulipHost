package com.tulip.host.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.tulip.host.data.EmployeeBasicDTO;
import com.tulip.host.data.EmployeeDetailsDTO;
import com.tulip.host.domain.Employee;
import com.tulip.host.repository.EmployeeRepository;
import java.util.List;
import javax.persistence.EntityManager;

public class EmployeeRepositoryImpl extends BaseRepositoryImpl<Employee, Long> implements EmployeeRepository {

    protected EmployeeRepositoryImpl(EntityManager em) {
        super(Employee.class, em);
    }

    @Override
    public Employee add(Employee employee) {
        return save(employee);
    }

    @Override
    public List<EmployeeBasicDTO> fetchAll(boolean isActive) {
        return jpaQueryFactory
            .select(
                Projections.fields(
                    EmployeeBasicDTO.class,
                    EMPLOYEE.name,
                    EMPLOYEE.id,
                    EMPLOYEE.active,
                    EMPLOYEE.dob,
                    EMPLOYEE.gender,
                    EMPLOYEE.bloodGroup,
                    EMPLOYEE.phoneNumber
                )
            )
            .from(EMPLOYEE)
            .where(EMPLOYEE.active.eq(isActive))
            .fetch();
    }

    @Override
    public List<EmployeeBasicDTO> fetchAll() {
        return jpaQueryFactory
            .select(
                Projections.fields(
                    EmployeeBasicDTO.class,
                    EMPLOYEE.name,
                    EMPLOYEE.id,
                    EMPLOYEE.active,
                    EMPLOYEE.dob,
                    EMPLOYEE.gender,
                    EMPLOYEE.bloodGroup,
                    EMPLOYEE.phoneNumber
                )
            )
            .from(EMPLOYEE)
            .fetch();
    }

    @Override
    public List<EmployeeBasicDTO> searchByName(String name) {
        return jpaQueryFactory
            .select(
                Projections.fields(
                    EmployeeBasicDTO.class,
                    EMPLOYEE.name,
                    EMPLOYEE.id,
                    EMPLOYEE.active,
                    EMPLOYEE.dob,
                    EMPLOYEE.gender,
                    EMPLOYEE.bloodGroup,
                    EMPLOYEE.phoneNumber
                )
            )
            .from(EMPLOYEE)
            .where(EMPLOYEE.name.likeIgnoreCase(Expressions.asString("%").concat(name).concat("%")))
            .fetch();
    }

    @Override
    public EmployeeDetailsDTO search(long id) {
        return jpaQueryFactory
            .select(
                Projections.fields(
                    EmployeeDetailsDTO.class,
                    EMPLOYEE.name,
                    EMPLOYEE.id,
                    EMPLOYEE.active,
                    EMPLOYEE.dob,
                    EMPLOYEE.gender,
                    EMPLOYEE.bloodGroup,
                    EMPLOYEE.phoneNumber,
                    EMPLOYEE.group
                )
            )
            .from(EMPLOYEE)
            .where(EMPLOYEE.id.eq(id))
            .fetchOne();
    }

    @Override
    public EmployeeDetailsDTO edit() {
        return null;
    }
}
