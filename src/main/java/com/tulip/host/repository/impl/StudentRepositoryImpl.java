package com.tulip.host.repository.impl;

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
            .where(STUDENT.id.eq(id).and(CLASS_DETAIL.session().id.eq(2L)))
            .fetchOne();
    }
}
