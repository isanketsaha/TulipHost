package com.tulip.host.repository.impl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.tulip.host.data.EmployeeDetailsDTO;
import com.tulip.host.domain.Employee;
import com.tulip.host.enums.UserRoleEnum;
import com.tulip.host.repository.EmployeeRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class EmployeeRepositoryImpl extends BaseRepositoryImpl<Employee, Long> implements EmployeeRepository {

    protected EmployeeRepositoryImpl(EntityManager em) {
        super(Employee.class, em);
    }

    @Override
    public List<Employee> fetchAll(boolean isActive, List<UserRoleEnum> role) {
        return jpaQueryFactory
            .selectFrom(EMPLOYEE)
            .where(
                EMPLOYEE.active
                    .eq(isActive)
                    .and(EMPLOYEE.group().authority.in(role.stream().map(item -> item.getValue()).collect(Collectors.toList())))
            )
            .distinct()
            .fetch();
    }

    @Override
    public List<Employee> fetchAll() {
        return jpaQueryFactory
            .selectFrom(EMPLOYEE)
            .where(EMPLOYEE.group().authority.in(UserRoleEnum.STAFF.getValue(), UserRoleEnum.TEACHER.getValue()))
            .fetch();
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
            .where(
                EMPLOYEE.active.eq(true).and(EMPLOYEE.group().authority.in(UserRoleEnum.STAFF.getValue(), UserRoleEnum.TEACHER.getValue()))
            )
            .groupBy(EMPLOYEE.group())
            .fetch();
        return tupleList
            .stream()
            .collect(
                Collectors.toMap(
                    item -> item.get(EMPLOYEE.group().authority).split("_")[1].toUpperCase(),
                    item -> item.get(EMPLOYEE.group().count())
                )
            );
    }

    @Override
    public Optional<Employee> findByUserId(String userId) {
        return  Optional.ofNullable(jpaQueryFactory.selectFrom(EMPLOYEE)
            .where(EMPLOYEE.credential().userId.eq(userId)).fetchOne());
    }

    @Override
    public EmployeeDetailsDTO edit() {
        return null;
    }
}
