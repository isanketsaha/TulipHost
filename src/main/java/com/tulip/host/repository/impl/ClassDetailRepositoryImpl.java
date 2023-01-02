package com.tulip.host.repository.impl;

import com.querydsl.core.types.Projections;
import com.tulip.host.data.ClassDetailDTO;
import com.tulip.host.data.EmployeeDetailsDTO;
import com.tulip.host.domain.ClassDetail;
import com.tulip.host.domain.Employee;
import com.tulip.host.repository.ClassDetailRepository;
import com.tulip.host.repository.EmployeeRepository;
import java.util.List;
import javax.persistence.EntityManager;

public class ClassDetailRepositoryImpl extends BaseRepositoryImpl<ClassDetail, Long> implements ClassDetailRepository {

    protected ClassDetailRepositoryImpl(EntityManager em) {
        super(ClassDetail.class, em);
    }

    @Override
    public List<ClassDetailDTO> fetchClassListBySession(Long sessionId) {
        return jpaQueryFactory
            .select(Projections.bean(ClassDetailDTO.class))
            .from(CLASS_DETAIL)
            .where(CLASS_DETAIL.session.eq(sessionId))
            .fetch();
    }

    @Override
    public ClassDetailDTO fetchClass(Long sessionId, String std) {
        return jpaQueryFactory
            .select(Projections.bean(ClassDetailDTO.class))
            .from(CLASS_DETAIL)
            .where(CLASS_DETAIL.session.eq(sessionId).and(CLASS_DETAIL.std.eq(std)))
            .fetchFirst();
    }
}
