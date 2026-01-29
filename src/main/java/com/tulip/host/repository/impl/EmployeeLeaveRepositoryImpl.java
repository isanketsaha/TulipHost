package com.tulip.host.repository.impl;

import com.querydsl.core.Tuple;
import com.tulip.host.data.LeaveBalanceDTO;
import com.tulip.host.domain.EmployeeLeave;
import com.tulip.host.domain.LeaveType;
import com.tulip.host.domain.Session;
import com.tulip.host.enums.LeaveStatus;
import com.tulip.host.mapper.LeaveBalanceMapper;
import com.tulip.host.repository.EmployeeLeaveRepository;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;

public class EmployeeLeaveRepositoryImpl extends BaseRepositoryImpl<EmployeeLeave, Long> implements EmployeeLeaveRepository {

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
            .orderBy(EMPLOYEE_LEAVE.endDate.desc())
            .fetch();
    }

    @Override
    public Map<String, List<LeaveBalanceDTO>> findLeaveBalance() {
        List<Tuple> results = jpaQueryFactory
            .select(EMPLOYEE_LEAVE.leaveType(), EMPLOYEE_LEAVE.totalDays.sum(), EMPLOYEE.name)
            .from(EMPLOYEE)
            .innerJoin(EMPLOYEE_LEAVE)
            .on(
                EMPLOYEE.active
                    .and(EMPLOYEE_LEAVE.employee().eq(EMPLOYEE))
                    .and(
                        EMPLOYEE_LEAVE.status
                            .in(LeaveStatus.APPROVED, LeaveStatus.PENDING)
                            .and(EMPLOYEE_LEAVE.leaveType().session().eq(getCurrentSession()))
                    )
            )
            .groupBy(EMPLOYEE_LEAVE.leaveType(), EMPLOYEE)
            .orderBy(EMPLOYEE.name.asc())
            .fetch();

        return results
            .stream()
            .collect(
                Collectors.groupingBy(
                    tuple -> tuple.get(2, String.class), // Group by employeeName
                    Collectors.mapping(
                        tuple -> {
                            LeaveType leaveType = tuple.get(0, LeaveType.class);
                            BigDecimal count = tuple.get(1, BigDecimal.class);
                            return leaveBalanceMapper.createLeaveBalance(leaveType, count);
                        },
                        Collectors.toList()
                    )
                )
            );
    }

    @Override
    public List<LeaveBalanceDTO> findLeaveBalance(Long employeeId) {
        List<Tuple> results = jpaQueryFactory
            .select(LEAVE_TYPE, EMPLOYEE_LEAVE.totalDays.sum())
            .from(LEAVE_TYPE)
            .leftJoin(EMPLOYEE_LEAVE)
            .on(
                EMPLOYEE_LEAVE.leaveType()
                    .eq(LEAVE_TYPE)
                    .and(EMPLOYEE_LEAVE.employee().id.eq(employeeId))
                    .and(EMPLOYEE_LEAVE.status.in(LeaveStatus.APPROVED, LeaveStatus.PENDING))
            )
            .groupBy(LEAVE_TYPE.id, LEAVE_TYPE.name, LEAVE_TYPE.count)
            .fetch();

        return results
            .stream()
            .map(tuple -> {
                LeaveType leaveType = tuple.get(0, LeaveType.class);
                BigDecimal usedDays = tuple.get(1, BigDecimal.class);
                return leaveBalanceMapper.createLeaveBalance(leaveType, usedDays);
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<LeaveBalanceDTO> findLeaveBalanceByTid(String tid) {
        List<Tuple> results = jpaQueryFactory
            .select(LEAVE_TYPE, EMPLOYEE_LEAVE.totalDays.sum())
            .from(LEAVE_TYPE)
            .leftJoin(EMPLOYEE_LEAVE)
            .on(
                EMPLOYEE_LEAVE.leaveType()
                    .eq(LEAVE_TYPE)
                    .and(EMPLOYEE_LEAVE.employee().tid.eq(tid))
                    .and(EMPLOYEE_LEAVE.status.in(LeaveStatus.APPROVED, LeaveStatus.PENDING))
            )
            .groupBy(LEAVE_TYPE.id, LEAVE_TYPE.name, LEAVE_TYPE.count)
            .fetch();

        return results
            .stream()
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
        return jpaQueryFactory.selectFrom(EMPLOYEE_LEAVE).fetch();
    }

    @Override
    public List<EmployeeLeave> findByDateRange(String fromDate, String toDate) {
        LocalDate fromLocalDate = LocalDate.parse(fromDate, java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        LocalDate toLocalDate = LocalDate.parse(toDate, java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        return jpaQueryFactory
            .selectFrom(EMPLOYEE_LEAVE)
            .where(EMPLOYEE_LEAVE.startDate.loe(toLocalDate).and(EMPLOYEE_LEAVE.endDate.goe(fromLocalDate)))
            .orderBy(EMPLOYEE_LEAVE.createdBy.desc())
            .fetch();
    }

    @Override
    public List<EmployeeLeave> findByStatus(LeaveStatus status) {
        return jpaQueryFactory
            .selectFrom(EMPLOYEE_LEAVE)
            .where(EMPLOYEE_LEAVE.status.eq(status))
            .orderBy(EMPLOYEE_LEAVE.createdBy.desc())
            .fetch();
    }

    @Override
    public List<EmployeeLeave> findLeaveBalanceByTid(String tid, LocalDate fromDate, LocalDate toDate) {
        return jpaQueryFactory
            .selectFrom(EMPLOYEE_LEAVE)
            .where(
                EMPLOYEE_LEAVE.status
                    .eq(LeaveStatus.APPROVED)
                    .and(EMPLOYEE_LEAVE.employee().tid.eq(tid))
                    .and(EMPLOYEE_LEAVE.startDate.goe(fromDate).and(EMPLOYEE_LEAVE.endDate.loe(toDate)))
            )
            .fetch();
    }
}
