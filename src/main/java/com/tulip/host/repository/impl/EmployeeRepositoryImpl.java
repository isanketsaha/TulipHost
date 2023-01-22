package com.tulip.host.repository.impl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.tulip.host.data.EmployeeDetailsDTO;
import com.tulip.host.domain.Employee;
import com.tulip.host.repository.EmployeeRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;

public class EmployeeRepositoryImpl extends BaseRepositoryImpl<Employee, Long> implements EmployeeRepository {

    protected EmployeeRepositoryImpl(EntityManager em) {
        super(Employee.class, em);
    }

    @Override
    public List<Employee> fetchAll(boolean isActive) {
        return jpaQueryFactory.selectFrom(EMPLOYEE).where(EMPLOYEE.active.eq(isActive)).distinct().fetch();
    }

    @Override
    public List<Employee> fetchAll() {
        return jpaQueryFactory.selectFrom(EMPLOYEE).fetch();
    }

    @Override
    public List<Employee> searchByName(String name) {
        return jpaQueryFactory
            .selectFrom(EMPLOYEE)
            .where(EMPLOYEE.name.likeIgnoreCase(Expressions.asString("%").concat(name).concat("%")))
            .fetch();
    }

    @Override
    public Employee search(long id) {
        return jpaQueryFactory.selectFrom(EMPLOYEE).where(EMPLOYEE.id.eq(id)).fetchOne();
    }

    @Override
    public Map<String, Long> fetchStaffReport() {
        List<Tuple> tupleList = jpaQueryFactory
            .select(EMPLOYEE.group().authority, EMPLOYEE.group().count())
            .from(EMPLOYEE)
            .where(EMPLOYEE.active.eq(true))
            .groupBy(EMPLOYEE.group())
            .fetch();
        Map<String, Long> collect = tupleList
            .stream()
            .collect(
                Collectors.toMap(
                    item -> item.get(EMPLOYEE.group().authority).split("_")[1].toUpperCase(),
                    item -> item.get(EMPLOYEE.group().count())
                )
            );
        return collect;
    }

    @Override
    public EmployeeDetailsDTO edit() {
        return null;
    }
}
