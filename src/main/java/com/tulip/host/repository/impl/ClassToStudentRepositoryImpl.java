package com.tulip.host.repository.impl;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.types.Projections.list;

import com.querydsl.core.Tuple;
import com.tulip.host.domain.Session;
import com.tulip.host.domain.StudentToClass;
import com.tulip.host.domain.StudentToClassId;
import com.tulip.host.enums.FeesRuleType;
import com.tulip.host.repository.ClassToStudentRepository;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
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
            .where(STUDENT.createdDate.between(session.getFromDate(), session.getToDate()))
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

    @Override
    public Map<String, Double> recurringRevenueYearly(Session session) {
        List<Tuple> activeStudentClassWise = jpaQueryFactory
            .select(CLASS_DETAIL.std, FEES_CATALOG.price.multiply(STUDENT_TO_CLASS.student().count()))
            .from(STUDENT_TO_CLASS)
            .join(STUDENT_TO_CLASS.classField(), CLASS_DETAIL)
            .on(CLASS_DETAIL.session().eq(session))
            .join(CLASS_DETAIL.feesCatalogs, FEES_CATALOG)
            .on(
                FEES_CATALOG.active
                    .eq(true)
                    .and(
                        FEES_CATALOG.applicableRule.eq(FeesRuleType.YEARLY).and(FEES_CATALOG.feesName.notEqualsIgnoreCase("ADMISSION FEES"))
                    )
            )
            .join(STUDENT_TO_CLASS.student(), STUDENT)
            .on(STUDENT.active.eq(true))
            .where(STUDENT_TO_CLASS.student().createdDate.loe(session.getFromDate()))
            .groupBy(CLASS_DETAIL, FEES_CATALOG)
            .fetch();
        log.info("{}", activeStudentClassWise);
        return activeStudentClassWise
            .stream()
            .collect(
                Collectors.toMap(
                    item -> item.get(0, String.class),
                    item1 -> item1.get(1, Double.class),
                    (existingValue, newValue) -> existingValue + newValue
                )
            );
    }

    public Map<String, Long> initialSessionStrength(Session session, Date date) {
        return jpaQueryFactory
            .select(CLASS_DETAIL.std, STUDENT_TO_CLASS.student().count())
            .from(STUDENT_TO_CLASS)
            .join(STUDENT_TO_CLASS.classField(), CLASS_DETAIL)
            .on(CLASS_DETAIL.session().eq(session))
            .join(STUDENT_TO_CLASS.student(), STUDENT)
            .on(STUDENT.active.eq(true).and(STUDENT.createdDate.lt(date)))
            .groupBy(STUDENT_TO_CLASS.classField())
            .transform(groupBy(CLASS_DETAIL.std).as(STUDENT_TO_CLASS.student().count()));
    }
}
