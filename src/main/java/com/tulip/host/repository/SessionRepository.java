package com.tulip.host.repository;

import com.tulip.host.domain.Session;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Long> {
    Session fetchCurrentSession();

    List<Session> listAllFinancialYears();

    Session sessionByDate(Date date);
}
