package com.tulip.host.repository.impl;

import com.querydsl.core.annotations.Config;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tulip.host.domain.*;
import com.tulip.host.repository.BaseRepository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

public abstract class BaseRepositoryImpl<T, ID> extends SimpleJpaRepository<T, ID> implements BaseRepository<T, ID> {

    static final QCatalog CATALOG = QCatalog.catalog;
    static final QClassDetail CLASS_DETAIL = QClassDetail.classDetail;
    static final QCredential CREDENTIAL = QCredential.credential;
    static final QFee FEE = QFee.fee;
    static final com.tulip.host.domain.QEmployee EMPLOYEE = QEmployee.employee;
    static final QStudent STUDENT = QStudent.student;
    static final QParentsDetail PARENTS_DETAIL = QParentsDetail.parentsDetail;
    static final QUserGroup USER_GROUP = QUserGroup.userGroup;
    static final QTransactionHistory TRANSACTION_HISTORY = QTransactionHistory.transactionHistory;
    static final QPaymentMode PAYMENT_MODE = QPaymentMode.paymentMode;
    static final QPurchaseOrder PURCHASE_ORDER = QPurchaseOrder.purchaseOrder;
    static final QLineItem LINE_ITEM = QLineItem.lineItem;
    static final QHoliday HOLIDAY = QHoliday.holiday;
    static final QSession SESSION = QSession.session;

    @PersistenceContext
    EntityManager em;

    JPAQueryFactory jpaQueryFactory;

    JPAQuery<T> jpaQuery;

    protected BaseRepositoryImpl(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
        this.em = em;
        this.jpaQueryFactory = new JPAQueryFactory(em);
        this.jpaQuery = new JPAQuery<>(em);
    }
}
