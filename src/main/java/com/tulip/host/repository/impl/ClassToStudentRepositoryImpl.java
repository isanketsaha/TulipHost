package com.tulip.host.repository.impl;

import static com.querydsl.core.group.GroupBy.groupBy;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.querydsl.core.Tuple;
import com.tulip.host.domain.Session;
import com.tulip.host.domain.StudentToClass;
import com.tulip.host.domain.StudentToClassId;
import com.tulip.host.repository.ClassToStudentRepository;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClassToStudentRepositoryImpl extends BaseRepositoryImpl<StudentToClass, StudentToClassId> implements ClassToStudentRepository {

    protected ClassToStudentRepositoryImpl(EntityManager em) {
        super(StudentToClass.class, em);
    }

    public Map<String, Map<String, Long>> overYearAdmission(Session session) {
        List<Tuple> tupleList = jpaQueryFactory
            .select(CLASS_DETAIL.std, STUDENT_TO_CLASS.student().createdDate.yearMonth(), STUDENT_TO_CLASS.student().count())
            .from(STUDENT_TO_CLASS)
            .join(STUDENT_TO_CLASS.classField(), CLASS_DETAIL)
            .on(CLASS_DETAIL.session().eq(session))
            .join(STUDENT_TO_CLASS.student(), STUDENT)
            .on(STUDENT.active.eq(true))
            .where(STUDENT.createdDate.between(session.getFromDate().atStartOfDay(), session.getToDate().atTime(LocalTime.MAX)))
            .groupBy(STUDENT_TO_CLASS.classField(), STUDENT_TO_CLASS.student().createdDate.yearMonth())
            .orderBy(STUDENT_TO_CLASS.student().createdDate.yearMonth().asc())
            .fetch();
        Map<String, Map<String, Long>> summary = new LinkedHashMap<>();
        for (Tuple tuple : tupleList) {
            String std = tuple.get(0, String.class);
            String month = String.valueOf(tuple.get(1, Integer.class));
            Long strength = tuple.get(2, Long.class);
            if (!summary.containsKey(std)) {
                Map<String, Long> map = new LinkedHashMap<>();
                map.put(month, strength);
                summary.put(std, map);
            } else {
                summary.get(std).put(month, strength);
            }
        }
        return summary;
    }

    public Map<String, Long> initialSessionStrength(Session session, LocalDate date) {
        return jpaQueryFactory
            .select(CLASS_DETAIL.std, STUDENT_TO_CLASS.student().countDistinct())
            .from(STUDENT_TO_CLASS)
            .join(STUDENT_TO_CLASS.classField(), CLASS_DETAIL)
            .on(CLASS_DETAIL.session().eq(session))
            .join(STUDENT_TO_CLASS.student(), STUDENT)
            .on(STUDENT.active.eq(true))
            .join(STUDENT.transactions, TRANSACTION)
            .join(TRANSACTION.feesLineItem, FEES_LINE_ITEM)
            .join(FEES_LINE_ITEM.feesProduct(), FEES_CATALOG)
            .on(FEES_CATALOG.feesName.likeIgnoreCase("SESSION%").and(FEES_CATALOG.std().eq(CLASS_DETAIL)))
            .groupBy(STUDENT_TO_CLASS.classField())
            .transform(groupBy(CLASS_DETAIL.std).as(STUDENT_TO_CLASS.student().count()));
    }

    public Map<String, Double> paymentDoneTillDate(Session session) {
        return jpaQueryFactory
            .select(CLASS_DETAIL.std, FEES_CATALOG.price.sum())
            .from(STUDENT_TO_CLASS)
            .join(STUDENT_TO_CLASS.classField(), CLASS_DETAIL)
            .on(CLASS_DETAIL.session().eq(session))
            .join(STUDENT_TO_CLASS.student(), STUDENT)
            .on(STUDENT.active.eq(true))
            .join(STUDENT.transactions, TRANSACTION)
            .on(TRANSACTION.feesLineItem.isNotEmpty())
            .join(TRANSACTION.feesLineItem, FEES_LINE_ITEM)
            .join(FEES_LINE_ITEM.feesProduct(), FEES_CATALOG)
            .on(FEES_CATALOG.std().eq(CLASS_DETAIL))
            .groupBy(CLASS_DETAIL.std)
            .transform(groupBy(CLASS_DETAIL.std).as(FEES_CATALOG.price.sum()));
    }
}
