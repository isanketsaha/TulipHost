package com.tulip.host.repository.impl;

import com.querydsl.core.types.Projections;
import com.tulip.host.data.LoginDTO;
import com.tulip.host.domain.Credential;
import com.tulip.host.domain.Employee;
import com.tulip.host.repository.CredentialRepository;
import jakarta.persistence.EntityManager;
import java.util.Optional;

public class CredentialRepositoryImpl extends BaseRepositoryImpl<Credential, Long> implements CredentialRepository {

    protected CredentialRepositoryImpl(EntityManager em) {
        super(Credential.class, em);
    }

    @Override
    public Optional<LoginDTO> findByUserId(String userId) {
        return Optional.ofNullable(
            jpaQueryFactory
                .select(
                    Projections.fields(
                        LoginDTO.class,
                        CREDENTIAL.userId,
                        EMPLOYEE.resetCredential,
                        CREDENTIAL.createdDate,
                        EMPLOYEE.name,
                        EMPLOYEE.active,
                        CREDENTIAL.password,
                        USER_GROUP.authority,
                        EMPLOYEE.locked
                    )
                )
                .from(CREDENTIAL)
                .innerJoin(EMPLOYEE)
                .on(EMPLOYEE.credential().eq(CREDENTIAL))
                .innerJoin(USER_GROUP)
                .on(USER_GROUP.eq(EMPLOYEE.group()))
                .where(CREDENTIAL.userId.eq(userId))
                .fetchFirst()
        );
    }

    @Override
    public Optional<String> findAuthoritiesByUserId(String userId) {
        return Optional.ofNullable(
            jpaQueryFactory
                .select(USER_GROUP.authority)
                .from(CREDENTIAL)
                .innerJoin(EMPLOYEE)
                .on(EMPLOYEE.credential().eq(CREDENTIAL))
                .innerJoin(USER_GROUP)
                .on(USER_GROUP.eq(EMPLOYEE.group()))
                .where(CREDENTIAL.userId.eq(userId))
                .fetchFirst()
        );
    }

    @Override
    public Employee findUserProfileByUserId(String userId) {
        return jpaQueryFactory
            .selectFrom(EMPLOYEE)
            .innerJoin(CREDENTIAL)
            .on(EMPLOYEE.credential().eq(CREDENTIAL))
            .innerJoin(USER_GROUP)
            .on(USER_GROUP.eq(EMPLOYEE.group()))
            .where(CREDENTIAL.userId.eq(userId))
            .fetchFirst();
    }

    @Override
    public void disableProfileByUserId(String userId) {
        jpaQueryFactory
            .update(EMPLOYEE)
            .set(EMPLOYEE.active, false)
            .where(EMPLOYEE.credential().eq(CREDENTIAL).and(CREDENTIAL.userId.eq(userId)))
            .execute();
    }
}
