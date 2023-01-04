//package com.tulip.host.repository.impl;
//
//import com.querydsl.core.types.Projections;
//import com.tulip.host.data.ClassDetailDTO;
//import com.tulip.host.domain.ClassDetail;
//import com.tulip.host.repository.ClassDetailRepository;
//
//import java.util.List;
//import javax.persistence.EntityManager;
//
//public class ClassDetailRepositoryImpl extends BaseRepositoryImpl<ClassDetail, Long> implements ClassDetailRepository {
//
//    protected ClassDetailRepositoryImpl(EntityManager em) {
//        super(ClassDetail.class, em);
//    }
//
//    @Override
//    public List<ClassDetailDTO> fetchClassListBySession(Long sessionId) {
//        return jpaQueryFactory
//            .select(
//                Projections.fields(ClassDetailDTO.class, CLASS_DETAIL.id, CLASS_DETAIL.std, CLASS_DETAIL.headTeacher().name, CLASS_DETAIL.session().displayText)
//            )
//            .from(CLASS_DETAIL)
//            .where(CLASS_DETAIL.session().id.eq(sessionId))
//            .fetch();
//    }
//
//}
