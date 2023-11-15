package com.tulip.host.repository;

import com.tulip.host.data.LoginDTO;
import com.tulip.host.domain.Credential;
import com.tulip.host.domain.Employee;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CredentialRepository extends JpaRepository<Credential, Long> {
    Optional<LoginDTO> findByUserId(String userId);

    Optional<String> findAuthoritiesByUserId(String userId);

    Employee findUserProfileByUserId(String userId);

    void disableProfileByUserId(String userId);
}
