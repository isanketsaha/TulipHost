package com.tulip.host.repository;

import com.tulip.host.domain.UserToDependent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserToDependentRepository extends JpaRepository<UserToDependent, Long> {}
