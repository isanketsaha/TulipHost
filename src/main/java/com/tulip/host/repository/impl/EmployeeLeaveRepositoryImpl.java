package com.tulip.host.repository.impl;

import com.tulip.host.domain.AcademicCalendar;
import com.tulip.host.domain.EmployeeLeave;
import com.tulip.host.repository.AcademicCalendarRepository;
import com.tulip.host.repository.EmployeeLeaveRepository;
import jakarta.persistence.EntityManager;
import com.querydsl.core.Tuple;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EmployeeLeaveRepositoryImpl extends BaseRepositoryImpl<EmployeeLeave, Long>
                implements EmployeeLeaveRepository {
        protected EmployeeLeaveRepositoryImpl(EntityManager em) {
                super(EmployeeLeave.class, em);
        }

        @Override
        public List<EmployeeLeave> findByEmployeeId(String employeeId) {
                return jpaQueryFactory
                                .selectFrom(EMPLOYEE_LEAVE)
                                .where(EMPLOYEE_LEAVE.employee().credential().userId.eq(employeeId))
                                .fetch();
        }

        @Override
        public Map<String, Long> findLeaveBalance(String employeeId) {
                List<Tuple> results = jpaQueryFactory
                                .select(EMPLOYEE_LEAVE.leaveType().name, EMPLOYEE_LEAVE.totalDays.sum())
                                .from(EMPLOYEE_LEAVE)
                                .where(EMPLOYEE_LEAVE.employee().credential().userId.eq(employeeId))
                                .groupBy(EMPLOYEE_LEAVE.leaveType().name)
                                .fetch();

                return results.stream()
                                .collect(Collectors.toMap(
                                                tuple -> tuple.get(0, String.class),
                                                tuple -> tuple.get(1, java.math.BigDecimal.class).longValue()));
        }

}
