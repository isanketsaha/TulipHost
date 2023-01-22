package com.tulip.host.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.tulip.host.data.DashBoardStaffDTO;
import com.tulip.host.data.DashBoardStudentDTO;
import com.tulip.host.data.StudentDetailsDTO;
import com.tulip.host.domain.Student;
import com.tulip.host.repository.StudentRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
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
            .innerJoin(SESSION)
            .on(SESSION.id.eq(2L))
            .join(CLASS_DETAIL)
            .on(CLASS_DETAIL.session().eq(SESSION).and(CLASS_DETAIL.students.contains(STUDENT)))
            .where(STUDENT.name.likeIgnoreCase(Expressions.asString("%").concat(name).concat("%")))
            .distinct()
            .fetch();
    }

    @Override
    public Student search(long id) {
        return jpaQueryFactory
            .selectFrom(STUDENT)
            .leftJoin(STUDENT.classDetails, CLASS_DETAIL)
            .fetchJoin()
            .where(STUDENT.id.eq(id))
            .fetchOne();
    }

    @Override
    public long fetchStudentCount(boolean active, BooleanBuilder condition) {
        return condition != null
            ? jpaQueryFactory.selectFrom(STUDENT).where(STUDENT.active.eq(active).and(condition)).fetchCount()
            : jpaQueryFactory.selectFrom(STUDENT).where(STUDENT.active.eq(active)).fetchCount();
    }
}
