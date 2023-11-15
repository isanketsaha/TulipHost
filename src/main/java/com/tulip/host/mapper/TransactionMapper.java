package com.tulip.host.mapper;

import com.tulip.host.data.PaySummaryDTO;
import com.tulip.host.data.PrintTransactionDTO;
import com.tulip.host.data.StudentDetailsDTO;
import com.tulip.host.domain.Student;
import com.tulip.host.domain.Transaction;
import com.tulip.host.service.UploadService;
import com.tulip.host.web.rest.vm.PayVM;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
    componentModel = "spring",
    uses = {
        FeeLineItemMapper.class,
        StudentMapper.class,
        PurchaseLineItemMapper.class,
        ExpenseMapper.class,
        UploadMapper.class,
        DuesMapper.class,
    }
)
public interface TransactionMapper {
    @Mapping(target = "afterDiscount", source = "total")
    @Mapping(target = "amount", source = "total")
    @Mapping(target = "feesLineItem", source = "feeItem")
    @Mapping(target = "purchaseLineItems", source = "purchaseItems")
    @Mapping(target = "student", source = "studentId")
    @Mapping(target = "type", source = "payType")
    Transaction toModel(PayVM order);

    List<Transaction> toFeesModelList(List<PayVM> classDetails);

    @Mapping(target = "paymentDateTime", source = "createdDate")
    @Mapping(target = "total", source = "amount")
    @Mapping(target = "payType", source = "type")
    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentName", source = "student.name")
    @Mapping(target = "feesItem", source = "feesLineItem")
    @Mapping(target = "purchaseItems", source = "purchaseLineItems")
    @Mapping(target = "paymentId", source = "id")
    @Mapping(target = "expenseItems", source = "expenseItems")
    @Mapping(target = "docs", source = "uploadList")
    PaySummaryDTO toEntity(Transaction feesOrder);

    List<PaySummaryDTO> toEntityList(Iterable<Transaction> feesOrder);

    @Mapping(target = "studentName", source = "student.name")
    @Mapping(target = "feesItem", source = "feesLineItem")
    @Mapping(target = "purchaseItems", source = "purchaseLineItems")
    @Mapping(target = "transactionId", source = "id")
    @Mapping(target = "payType", source = "type")
    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "total", source = "amount")
    @Mapping(target = "formattedPaymentDateTime", ignore = true)
    PrintTransactionDTO toPrintEntity(Transaction transaction);

    @AfterMapping
    default void map(@MappingTarget PrintTransactionDTO target, Transaction source) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy").withZone(ZoneId.systemDefault());
        target.setFormattedPaymentDateTime(formatter.format(source.getCreatedDate()));
    }
}
