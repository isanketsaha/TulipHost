package com.tulip.host.repository.impl;

import com.tulip.host.domain.AcademicCalendar;
import com.tulip.host.enums.TypeEnum;
import com.tulip.host.repository.AcademicCalendarRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;

@Slf4j
public class AcademicCalendarRepositoryImpl extends BaseRepositoryImpl<AcademicCalendar, Long>
    implements AcademicCalendarRepository {

    protected AcademicCalendarRepositoryImpl(EntityManager em) {
        super(AcademicCalendar.class, em);
    }

    @Override
    public List<AcademicCalendar> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return jpaQueryFactory
                .selectFrom(ACADEMIC_CALENDAR)
            .where(
                ACADEMIC_CALENDAR.eventType.eq(TypeEnum.HOLIDAY)
                    .and(ACADEMIC_CALENDAR.startDate.loe(endDate))
                    .and(ACADEMIC_CALENDAR.endDate.goe(startDate)))
                .fetch();
    }
}
