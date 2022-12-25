package com.tulip.host.repository;

import com.tulip.host.data.pojo.SessionPojo;
import com.tulip.host.domain.Session;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<SessionPojo> fetchCurrentSession();

    List<SessionPojo> listAllFinancialYears();
}
