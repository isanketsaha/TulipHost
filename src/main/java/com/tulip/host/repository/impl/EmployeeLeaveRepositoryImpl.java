package com.tulip.host.repository.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.querydsl.core.Tuple;
import com.tulip.host.data.LeaveBalanceDTO;
import com.tulip.host.domain.EmployeeLeave;
import com.tulip.host.domain.LeaveType;
import com.tulip.host.domain.Session;
import com.tulip.host.mapper.LeaveBalanceMapper;
import com.tulip.host.repository.EmployeeLeaveRepository;

import jakarta.persistence.EntityManager;

public class EmployeeLeaveRepositoryImpl extends BaseRepositoryImpl<EmployeeLeave, Long>
                implements EmployeeLeaveRepository {

        @Autowired
        private LeaveBalanceMapper leaveBalanceMapper;

        protected EmployeeLeaveRepositoryImpl(EntityManager em) {
                super(EmployeeLeave.class, em);
        }

        @Override
        public List<EmployeeLeave> findByEmployeeId(Long employeeId) {
                return jpaQueryFactory
                                .selectFrom(EMPLOYEE_LEAVE)
                                .where(EMPLOYEE_LEAVE.employee().id.eq(employeeId))
                                .fetch();
        }

        @Override
        public List<LeaveBalanceDTO> findLeaveBalance(Long employeeId) {
                List<Tuple> results = jpaQueryFactory
                                .select(
                                                LEAVE_TYPE,
                                                EMPLOYEE_LEAVE.totalDays.sum())
                                .from(LEAVE_TYPE)
                                .leftJoin(EMPLOYEE_LEAVE)
                                .on(EMPLOYEE_LEAVE.leaveType().eq(LEAVE_TYPE)
                                                .and(EMPLOYEE_LEAVE.employee().id.eq(employeeId)))
                                .groupBy(LEAVE_TYPE.id, LEAVE_TYPE.name, LEAVE_TYPE.count)
                                .fetch();

                return results.stream()
                                .map(tuple -> {
                                        LeaveType leaveType = tuple.get(0, LeaveType.class);
                                        BigDecimal usedDays = tuple.get(1, BigDecimal.class);
                                        return leaveBalanceMapper.createLeaveBalance(leaveType, usedDays);
                                })
                                .collect(Collectors.toList());
        }

        @Override
        public List<EmployeeLeave> findBySession(Session session) {
                // For now, return all leaves. We can optimize this later
                return jpaQueryFactory
                                .selectFrom(EMPLOYEE_LEAVE)
                                .fetch();
        }

        public List<EmployeeLeave> findByDateRange(String fromDate, String toDate) {
                // Convert string dates to LocalDate for comparison
                LocalDate fromLocalDate = LocalDate.parse(fromDate,
                                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                LocalDate toLocalDate = LocalDate.parse(toDate,
                                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                return jpaQueryFactory
                                .selectFrom(EMPLOYEE_LEAVE)
                                .where(EMPLOYEE_LEAVE.startDate.loe(toLocalDate)
                                                .and(EMPLOYEE_LEAVE.endDate.goe(fromLocalDate)))
                                .fetch();
        }
}
