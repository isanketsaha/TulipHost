package com.tulip.host.repository;

import com.tulip.host.data.pojo.LoginPojo;
import com.tulip.host.domain.Credential;
import com.tulip.host.domain.Employee;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CredentialRepository extends JpaRepository<Credential, Long> {
    Optional<LoginPojo> findByUserId(String userId);

    Optional<Credential> findByUserIdAndPassword(String userId, String password);

    Optional<Credential> findAuthoritiesByUserId(String userId);

    Optional<Employee> findUserProfileByUserId(String userId);

    void disableProfileByUserId(String userId);
}
