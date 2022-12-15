package com.tulip.host.repository;

import com.tulip.host.data.pojo.EmployeePojo;
import com.tulip.host.data.pojo.LoginPojo;
import com.tulip.host.domain.ClassDetail;
import com.tulip.host.domain.Credential;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface CredentialRepository extends JpaRepository<Credential, Long> {
    Optional<LoginPojo> findByUserId(String userId);

    Optional<String> findAuthoritiesByUserId(String userId);

    Optional<EmployeePojo> findUserProfileByUserId(String userId);

    void disableProfileByUserId(String userId);
}
