package com.tulip.host.repository;

import com.tulip.host.domain.Bank;
import com.tulip.host.domain.Credential;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankRepository extends JpaRepository<Bank, Long> {}
