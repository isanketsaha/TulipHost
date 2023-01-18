package com.tulip.host.repository.impl;

import com.tulip.host.domain.ClassDetail;
import com.tulip.host.repository.ClassDetailRepository;
import java.util.List;
import javax.persistence.EntityManager;

public class ClassDetailRepositoryImpl extends BaseRepositoryImpl<ClassDetail, Long> implements ClassDetailRepository {

    protected ClassDetailRepositoryImpl(EntityManager em) {
        super(ClassDetail.class, em);
    }

    @Override
    public List<ClassDetail> findAllBySessionId(Long sessionId) {
        return jpaQueryFactory.selectFrom(CLASS_DETAIL).where(CLASS_DETAIL.session().id.eq(sessionId)).fetch();
    }

    @Override
    public ClassDetail findBySessionIdAndStd(Long sessionId, String std) {
        return jpaQueryFactory
            .selectFrom(CLASS_DETAIL)
            .where(CLASS_DETAIL.session().id.eq(sessionId).and(CLASS_DETAIL.std.eq(std)))
            .fetchOne();
    }
}
