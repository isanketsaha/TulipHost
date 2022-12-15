package com.tulip.host.repository.impl;

import com.tulip.host.domain.Credential;
import com.tulip.host.domain.Employee;
import com.tulip.host.repository.CredentialRepository;
import java.util.Optional;
import javax.persistence.EntityManager;

public class CredentialRepositoryImpl extends BaseRepositoryImpl<Credential, Long> implements CredentialRepository {

    protected CredentialRepositoryImpl(EntityManager em) {
        super(Credential.class, em);
    }

    @Override
    public Optional<Credential> findByUserId(String userId) {
        return Optional.empty();
    }

    @Override
    public Optional<Credential> findByUserIdAndPassword(String userId, String password) {
        return Optional.empty();
    }

    @Override
    public Optional<Credential> findAuthoritiesByUserId(String userId) {
        return Optional.empty();
    }

    @Override
    public Optional<Employee> findUserProfileByUserId(String userId) {
        return Optional.empty();
    }

    @Override
    public void disableProfileByUserId(String userId) {}
}
