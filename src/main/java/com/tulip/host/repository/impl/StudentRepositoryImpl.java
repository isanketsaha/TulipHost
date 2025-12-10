package com.tulip.host.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.tulip.host.domain.Student;
import com.tulip.host.repository.StudentRepository;
import com.tulip.host.utils.CommonUtils;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class StudentRepositoryImpl extends BaseRepositoryImpl<Student, Long> implements StudentRepository {

    protected StudentRepositoryImpl(EntityManager em) {
        super(Student.class, em);
    }

    @Override
    public List<Student> fetchAll(boolean isActive) {
        return jpaQueryFactory.selectFrom(STUDENT).where(STUDENT.active.eq(isActive)).distinct().fetch();
    }

    @Override
    public List<Student> search(String name) {
        return jpaQueryFactory
            .selectFrom(STUDENT)
            .where(STUDENT.name.likeIgnoreCase(Expressions.asString("%").concat(name).concat("%")))
            .distinct()
            .fetch();
    }

    @Override
    public Student search(long id) {
        return jpaQueryFactory.selectFrom(STUDENT).where(STUDENT.id.eq(id)).fetchOne();
    }

    @Override
    public Student searchByClassId(long studentId, long classId) {
        return jpaQueryFactory
            .selectFrom(STUDENT)
            .innerJoin(STUDENT.classDetails, CLASS_DETAIL)
            .where((STUDENT.id.eq(studentId).and(CLASS_DETAIL.id.eq(classId))))
            .fetchJoin()
            .fetchOne();
    }

    @Override
    public long fetchStudentCount(boolean active, BooleanBuilder condition) {
        return condition != null
            ? jpaQueryFactory
                .selectFrom(STUDENT)
                .where(STUDENT.active.eq(active).and(condition))
                .fetchCount()
            : jpaQueryFactory
                .selectFrom(STUDENT)
                .where(STUDENT.active.eq(active))
                .fetchCount();
    }

    @Override
    public Student checkIfFeesPaid(Long studentId, Long feesId, String month) {
        BooleanExpression monthCondition = StringUtils.isEmpty(month) ? FEES_LINE_ITEM.month.isNull() : FEES_LINE_ITEM.month.eq(month);
        return jpaQueryFactory
            .selectFrom(STUDENT)
            .innerJoin(STUDENT.transactions, TRANSACTION)
            .innerJoin(TRANSACTION.feesLineItem, FEES_LINE_ITEM)
            .where(
                STUDENT.active.eq(true).and(STUDENT.id.eq(studentId)).and(FEES_LINE_ITEM.feesProduct().id.eq(feesId)).and(monthCondition)
            )
            .fetchOne();
    }

    public Map<String, Long> admissionStats(LocalDate startDate, LocalDate endDate) {
        List<Tuple> tupleList = jpaQueryFactory
            .select(STUDENT.createdDate.month(), STUDENT.count())
            .from(STUDENT)
            .where(STUDENT.active.eq(true).and(STUDENT.createdDate.between(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX))))
            .groupBy(STUDENT.createdDate.year(), STUDENT.createdDate.month())
            .orderBy(STUDENT.createdDate.year().asc(), STUDENT.createdDate.month().asc())
            .fetch();
        Map<String, Long> resultAsMap = new LinkedHashMap<>();
        for (Tuple tuple : tupleList) {
            if (tuple != null) {
                String monthCreated = CommonUtils.getMonthName(tuple.get(0, Integer.class));
                Long studentCount = tuple.get(1, Long.class);
                resultAsMap.put(monthCreated, studentCount);
            }
        }
        return resultAsMap;
    }

    @Override
    public Student searchWithDetails(long id) {
        return jpaQueryFactory
                .selectFrom(STUDENT)
                .leftJoin(STUDENT.classDetails, CLASS_DETAIL).fetchJoin()
                .leftJoin(STUDENT.transports, STUDENT_TO_TRANSPORT).fetchJoin()
                .where(STUDENT.id.eq(id))
                .fetchOne();
    }
}
