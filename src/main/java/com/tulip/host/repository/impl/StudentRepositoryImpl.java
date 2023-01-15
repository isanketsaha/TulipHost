package com.tulip.host.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.tulip.host.data.ClassDetailDTO;
import com.tulip.host.data.DependentDTO;
import com.tulip.host.data.StudentBasicDTO;
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
    public List<StudentBasicDTO> fetchAll(boolean isActive) {
        return jpaQueryFactory
            .select(
                Projections.fields(
                    StudentBasicDTO.class,
                    STUDENT.name,
                    STUDENT.id,
                    STUDENT.active,
                    STUDENT.dob,
                    STUDENT.gender,
                    STUDENT.bloodGroup,
                    DEPENDENT.contact,
                    CLASS_DETAIL.std,
                    STUDENT.address
                )
            )
            .from(STUDENT)
            .join(DEPENDENT)
            .on(STUDENT.dependents.contains(DEPENDENT.student.any()))
            .join(CLASS_DETAIL)
            .on(STUDENT.std.contains(CLASS_DETAIL.studentList.any()))
            .where(STUDENT.active.eq(isActive))
            .fetch();
    }

    @Override
    public StudentDetailsDTO edit() {
        return null;
    }

    @Override
    public List<StudentBasicDTO> search(String name) {
        return jpaQueryFactory
            .select(
                Projections.fields(
                    StudentBasicDTO.class,
                    STUDENT.name,
                    STUDENT.id,
                    STUDENT.active,
                    STUDENT.dob,
                    STUDENT.gender,
                    STUDENT.bloodGroup,
                    DEPENDENT.contact,
                    CLASS_DETAIL.std,
                    STUDENT.address
                )
            )
            .from(STUDENT)
            .join(DEPENDENT)
            .on(STUDENT.dependents.contains(DEPENDENT.student.any()))
            .join(CLASS_DETAIL)
            .on(STUDENT.std.contains(CLASS_DETAIL.studentList.any()))
            .where(STUDENT.name.likeIgnoreCase(Expressions.asString("%").concat(name).concat("%")))
            .fetch();
    }

    @Override
    public StudentDetailsDTO search(long id) {
        return jpaQueryFactory
            .select(
                Projections.fields(
                    StudentDetailsDTO.class,
                    STUDENT.name,
                    STUDENT.id,
                    STUDENT.active,
                    STUDENT.dob,
                    STUDENT.gender,
                    STUDENT.bloodGroup,
                    DEPENDENT.contact,
                    STUDENT.previousSchool,
                    Projections.fields(
                        DependentDTO.class,
                        DEPENDENT.contact,
                        DEPENDENT.name,
                        DEPENDENT.relationship,
                        DEPENDENT.aadhaarNo,
                        DEPENDENT.id,
                        DEPENDENT.occupation,
                        DEPENDENT.occupation
                    ),
                    Projections.fields(ClassDetailDTO.class, CLASS_DETAIL.headTeacher(), CLASS_DETAIL.std, CLASS_DETAIL.id)
                )
            )
            .from(STUDENT)
            .join(DEPENDENT)
            .join(CLASS_DETAIL)
            .on(STUDENT.dependents.contains(DEPENDENT.student.any()))
            .on(STUDENT.std.contains(CLASS_DETAIL.studentList.any()))
            .fetchOne();
    }
}
