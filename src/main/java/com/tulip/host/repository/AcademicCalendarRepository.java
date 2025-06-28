package com.tulip.host.repository;

import com.tulip.host.domain.AcademicCalendar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AcademicCalendarRepository extends JpaRepository<AcademicCalendar, Long> {

    public List<AcademicCalendar> findByDateRange(LocalDate startDate, LocalDate endDate);
}

