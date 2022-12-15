package com.tulip.host.repository;

import com.tulip.host.domain.Credential;
import com.tulip.host.domain.Employee;
import com.tulip.host.domain.User;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CredentialRepository extends JpaRepository<Credential, Long> {
    Optional<Credential> findByUserId(String userId);

    Optional<Credential> findByUserIdAndPassword(String userId, String password);

    Optional<Credential> findAuthoritiesByUserId(String userId);

    Optional<Employee> findUserProfileByUserId(String userId);

    void disableProfileByUserId(String userId);
}
