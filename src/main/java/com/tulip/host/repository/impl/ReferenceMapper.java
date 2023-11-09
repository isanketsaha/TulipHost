package com.tulip.host.repository.impl;

import com.tulip.host.domain.AbstractAuditingEntity;
import com.tulip.host.domain.Employee;
import com.tulip.host.repository.EmployeeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.mapstruct.TargetType;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReferenceMapper {

    @PersistenceContext
    private final EntityManager entityManager;

    public <T extends AbstractAuditingEntity> T resolve(long id, @TargetType Class<T> entityClass) {
        return entityManager.getReference(entityClass, id);
    }
}
