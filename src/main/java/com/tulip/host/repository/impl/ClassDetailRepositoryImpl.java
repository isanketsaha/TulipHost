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

    @Override
    public ClassDetail findByClass(Long classId) {
        return jpaQueryFactory
            .selectFrom(CLASS_DETAIL)
            .leftJoin(CLASS_DETAIL.students, STUDENT)
            .leftJoin(STUDENT.transactions, TRANSACTION)
            .leftJoin(TRANSACTION.feesLineItem, FEES_LINE_ITEM)
            .on(FEES_LINE_ITEM.feesProduct().std().eq(CLASS_DETAIL))
            .where(CLASS_DETAIL.id.eq(classId))
            .fetchOne();
    }
}
