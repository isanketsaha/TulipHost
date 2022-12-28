package com.tulip.host.repository.impl;

import com.querydsl.core.types.Projections;
import com.tulip.host.data.EmployeeDetailsDTO;
import com.tulip.host.data.LoginDTO;
import com.tulip.host.domain.Credential;
import com.tulip.host.repository.CredentialRepository;
import java.util.Optional;
import javax.persistence.EntityManager;

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
                        CREDENTIAL.userName,
                        CREDENTIAL.resetPassword,
                        CREDENTIAL.createdDate,
                        EMPLOYEE.name,
                        EMPLOYEE.active,
                        CREDENTIAL.password,
                        USER_GROUP.authority
                    )
                )
                .from(CREDENTIAL)
                .innerJoin(EMPLOYEE)
                .on(EMPLOYEE.id.eq(CREDENTIAL.employeeId))
                .innerJoin(USER_GROUP)
                .on(USER_GROUP.id.eq(EMPLOYEE.groupId))
                .where(CREDENTIAL.userName.eq(userId))
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
                .on(EMPLOYEE.id.eq(CREDENTIAL.employeeId))
                .innerJoin(USER_GROUP)
                .on(USER_GROUP.id.eq(EMPLOYEE.groupId))
                .where(CREDENTIAL.userName.eq(userId))
                .fetchFirst()
        );
    }

    @Override
    public Optional<EmployeeDetailsDTO> findUserProfileByUserId(String userId) {
        return Optional.ofNullable(
            jpaQueryFactory
                .select(Projections.bean(EmployeeDetailsDTO.class))
                .from(CREDENTIAL)
                .innerJoin(EMPLOYEE)
                .on(EMPLOYEE.id.eq(CREDENTIAL.employeeId))
                .innerJoin(USER_GROUP)
                .on(USER_GROUP.id.eq(EMPLOYEE.groupId))
                .where(CREDENTIAL.userName.eq(userId))
                .fetchFirst()
        );
    }

    @Override
    public void disableProfileByUserId(String userId) {
        jpaQueryFactory
            .update(EMPLOYEE)
            .set(EMPLOYEE.active, false)
            .where(CREDENTIAL.employeeId.eq(EMPLOYEE.id).and(CREDENTIAL.userName.eq(userId)))
            .execute();
    }
}
