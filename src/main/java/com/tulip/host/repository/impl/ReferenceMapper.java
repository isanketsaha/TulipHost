package com.tulip.host.repository.impl;

import com.tulip.host.domain.AbstractAuditingEntity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.NonNull;
import org.mapstruct.ObjectFactory;
import org.mapstruct.TargetType;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

@Service
public class ReferenceMapper {

    @PersistenceContext
    private EntityManager entityManager;

    public <T extends AbstractAuditingEntity> T resolve(long id, @TargetType Class<T> entityClass) {
        return entityManager.getReference(entityClass, id);
    }
}
