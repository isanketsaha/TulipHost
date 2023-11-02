package com.tulip.host.repository.impl;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tulip.host.domain.QClassDetail;
import com.tulip.host.domain.QCredential;
import com.tulip.host.domain.QDependent;
import com.tulip.host.domain.QDues;
import com.tulip.host.domain.QDuesPayment;
import com.tulip.host.domain.QEmployee;
import com.tulip.host.domain.QExpense;
import com.tulip.host.domain.QFeesCatalog;
import com.tulip.host.domain.QFeesLineItem;
import com.tulip.host.domain.QHoliday;
import com.tulip.host.domain.QInventory;
import com.tulip.host.domain.QProductCatalog;
import com.tulip.host.domain.QPurchaseLineItem;
import com.tulip.host.domain.QSession;
import com.tulip.host.domain.QStudent;
import com.tulip.host.domain.QStudentToClass;
import com.tulip.host.domain.QTransaction;
import com.tulip.host.domain.QUserGroup;
import com.tulip.host.domain.QUserToDependent;
import com.tulip.host.repository.BaseRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

public abstract class BaseRepositoryImpl<T, ID> extends SimpleJpaRepository<T, ID> implements BaseRepository<T, ID> {

    static final QFeesCatalog FEES_CATALOG = QFeesCatalog.feesCatalog;
    static final QClassDetail CLASS_DETAIL = QClassDetail.classDetail;
    static final QCredential CREDENTIAL = QCredential.credential;
    static final com.tulip.host.domain.QEmployee EMPLOYEE = QEmployee.employee;
    static final QStudent STUDENT = QStudent.student;
    static final QExpense EXPENSE = QExpense.expense;
    static final QDependent DEPENDENT = QDependent.dependent;
    static final QUserGroup USER_GROUP = QUserGroup.userGroup;
    static final QPurchaseLineItem PURCHASE_LINE_ITEM = QPurchaseLineItem.purchaseLineItem;
    static final QHoliday HOLIDAY = QHoliday.holiday;
    static final QFeesLineItem FEES_LINE_ITEM = QFeesLineItem.feesLineItem;
    static final QSession SESSION = QSession.session;
    static final QTransaction TRANSACTION = QTransaction.transaction;
    static final QUserToDependent USER_TO_DEPENDENT = QUserToDependent.userToDependent;
    static final QStudentToClass STUDENT_TO_CLASS = QStudentToClass.studentToClass;
    static final QProductCatalog PRODUCT_CATALOG = QProductCatalog.productCatalog;
    static final QInventory INVENTORY = QInventory.inventory;

    static final QDues DUES = QDues.dues;
    static final QDuesPayment DUES_PAYMENT = QDuesPayment.duesPayment;

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
