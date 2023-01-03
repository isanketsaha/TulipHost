package com.tulip.host.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.tulip.host.data.ClassDetailDTO;
import com.tulip.host.data.ParentsDetailDto;
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
    public List<StudentBasicDTO> fetchAll() {
        return jpaQueryFactory
            .select(
                Projections.fields(
                    StudentBasicDTO.class,
                    STUDENT.name,
                    STUDENT.id,
                    STUDENT.isActive,
                    STUDENT.dob,
                    STUDENT.gender,
                    STUDENT.bloodGroup,
                    DEPENDENT.contact,
                    Projections.fields(
                        ParentsDetailDto.class,
                        DEPENDENT.contact,
                        DEPENDENT.name,
                        DEPENDENT.relationship,
                        DEPENDENT.aadhaarNo,
                        DEPENDENT.id,
                        DEPENDENT.occupation,
                        DEPENDENT.occupation
                    ),
                    Projections.fields(ClassDetailDTO.class, CLASS_DETAIL.headTeacher, CLASS_DETAIL.std, CLASS_DETAIL.id)
                )
            )
            .from(STUDENT)
            .join(DEPENDENT)
            .join(CLASS_DETAIL)
            .on(CLASS_DETAIL.id.eq(STUDENT.std))
            .fetch();
    }

    @Override
    public List<StudentBasicDTO> fetchAll(boolean isActive) {
        return jpaQueryFactory
            .select(
                Projections.fields(
                    StudentBasicDTO.class,
                    STUDENT.name,
                    STUDENT.id,
                    STUDENT.isActive,
                    STUDENT.dob,
                    STUDENT.gender,
                    STUDENT.bloodGroup,
                    DEPENDENT.contact,
                    Projections.fields(
                        ParentsDetailDto.class,
                        DEPENDENT.contact,
                        DEPENDENT.name,
                        DEPENDENT.relationship,
                        DEPENDENT.aadhaarNo,
                        DEPENDENT.id,
                        DEPENDENT.occupation,
                        DEPENDENT.occupation
                    ),
                    Projections.fields(ClassDetailDTO.class, CLASS_DETAIL.headTeacher, CLASS_DETAIL.std, CLASS_DETAIL.id)
                )
            )
            .from(STUDENT)
            .join(DEPENDENT)
            .join(CLASS_DETAIL)
            .on(CLASS_DETAIL.id.eq(STUDENT.std))
            .where(STUDENT.isActive.eq(isActive))
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
                    STUDENT.isActive,
                    STUDENT.dob,
                    STUDENT.gender,
                    STUDENT.bloodGroup,
                    DEPENDENT.contact,
                    Projections.fields(
                        ParentsDetailDto.class,
                        DEPENDENT.contact,
                        DEPENDENT.name,
                        DEPENDENT.relationship,
                        DEPENDENT.aadhaarNo,
                        DEPENDENT.id,
                        DEPENDENT.occupation,
                        DEPENDENT.occupation
                    ),
                    Projections.fields(ClassDetailDTO.class, CLASS_DETAIL.headTeacher, CLASS_DETAIL.std, CLASS_DETAIL.id)
                )
            )
            .from(STUDENT)
            .join(DEPENDENT)
            .join(CLASS_DETAIL)
            .on(CLASS_DETAIL.id.eq(STUDENT.std))
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
                    STUDENT.isActive,
                    STUDENT.dob,
                    STUDENT.gender,
                    STUDENT.bloodGroup,
                    DEPENDENT.contact,
                    STUDENT.previousSchool,
                    Projections.fields(
                        ParentsDetailDto.class,
                        DEPENDENT.contact,
                        DEPENDENT.name,
                        DEPENDENT.relationship,
                        DEPENDENT.aadhaarNo,
                        DEPENDENT.id,
                        DEPENDENT.occupation,
                        DEPENDENT.occupation
                    ),
                    Projections.fields(ClassDetailDTO.class, CLASS_DETAIL.headTeacher, CLASS_DETAIL.std, CLASS_DETAIL.id)
                )
            )
            .from(STUDENT)
            .join(DEPENDENT)
            .join(CLASS_DETAIL)
            .on(CLASS_DETAIL.id.eq(STUDENT.std))
            .on(DEPENDENT.student.eq(STUDENT.id))
            .where(STUDENT.id.eq(id))
            .fetchOne();
    }
}
