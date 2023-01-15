package com.tulip.host.mapper;

import com.tulip.host.data.PaySummaryDTO;
import com.tulip.host.domain.Transaction;
import com.tulip.host.web.rest.vm.PayVM;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { FeeLineItemMapper.class, StudentMapper.class, PurchaseLineItemMapper.class })
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
    @Mapping(target = "paymentReceivedBy", source = "createdBy")
    PaySummaryDTO toEntity(Transaction feesOrder);

    List<PaySummaryDTO> toEntityList(List<Transaction> feesOrder);
}
