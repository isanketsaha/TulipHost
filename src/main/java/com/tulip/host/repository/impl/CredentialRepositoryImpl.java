package com.tulip.host.repository.impl;

import com.querydsl.core.types.Projections;
import com.tulip.host.data.pojo.EmployeePojo;
import com.tulip.host.data.pojo.LoginPojo;
import com.tulip.host.domain.Credential;
import com.tulip.host.repository.CredentialRepository;
import java.util.Optional;
import javax.persistence.EntityManager;

public class CredentialRepositoryImpl extends BaseRepositoryImpl<Credential, Long> implements CredentialRepository {

    protected CredentialRepositoryImpl(EntityManager em) {
        super(Credential.class, em);
    }

    @Override
    public Optional<LoginPojo> findByUserId(String userId) {
        return Optional.ofNullable(
            jpaQueryFactory
                .select(
                    Projections.fields(
                        LoginPojo.class,
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
    public Optional<EmployeePojo> findUserProfileByUserId(String userId) {
        return Optional.ofNullable(
            jpaQueryFactory
                .select(Projections.bean(EmployeePojo.class))
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
