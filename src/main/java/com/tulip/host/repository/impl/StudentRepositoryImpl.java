package com.tulip.host.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.tulip.host.data.StudentDetailsDTO;
import com.tulip.host.domain.Student;
import com.tulip.host.repository.StudentRepository;
import java.util.List;
import javax.persistence.EntityManager;

public class StudentRepositoryImpl extends BaseRepositoryImpl<Student, Long> implements StudentRepository {

    protected StudentRepositoryImpl(EntityManager em) {
        super(Student.class, em);
    }

    @Override
    public List<Student> fetchAll(boolean isActive) {
        return jpaQueryFactory.selectFrom(STUDENT).where(STUDENT.active.eq(isActive)).distinct().fetch();
    }

    @Override
    public StudentDetailsDTO edit() {
        return null;
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
        return jpaQueryFactory
            .selectFrom(STUDENT)
            .leftJoin(STUDENT.classDetails, CLASS_DETAIL)
            .orderBy(CLASS_DETAIL.createdDate.desc())
            .where(STUDENT.id.eq(id))
            .fetchOne();
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
            ? jpaQueryFactory.selectFrom(STUDENT).where(STUDENT.active.eq(active).and(condition)).fetchCount()
            : jpaQueryFactory.selectFrom(STUDENT).where(STUDENT.active.eq(active)).fetchCount();
    }

    @Override
    public Student checkIfFeesPaid(Long studentId, Long feesId) {
        return jpaQueryFactory
            .selectFrom(STUDENT)
            .innerJoin(STUDENT.transactions, TRANSACTION)
            .innerJoin(TRANSACTION.feesLineItem, FEES_LINE_ITEM)
            .where(STUDENT.active.eq(true).and(FEES_LINE_ITEM.feesProduct().id.eq(feesId)))
            .fetchOne();
    }
}
