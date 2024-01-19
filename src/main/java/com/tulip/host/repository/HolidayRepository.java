package com.tulip.host.repository;

import com.tulip.host.domain.AcademicCalendar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayRepository extends JpaRepository<AcademicCalendar, Long> {}
