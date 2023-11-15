package com.tulip.host.repository.impl;

import com.tulip.host.domain.AbstractAuditingEntity;
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
        return entityManager.find(entityClass, id);
    }
    /**
     * Loads database entity from any type from the DTO id.
     * EntityDto is just a contract with id() method.
     */
    //    @ObjectFactory
    //    public <E extends AbstractAuditingEntity> E load(@TargetType @NotNull Class<E> type, @NotNull long id) {
    //        return em.find(type, id);
    //    }

}
