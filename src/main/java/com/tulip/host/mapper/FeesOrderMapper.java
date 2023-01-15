//package com.tulip.host.mapper;
//
//import com.tulip.host.data.PaySummaryDTO;
//import com.tulip.host.web.rest.vm.PayVM;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//
//import java.util.List;
//
//@Mapper(componentModel = "spring", uses = { FeeLineItemMapper.class, StudentMapper.class })
//public interface FeesOrderMapper {
//    @Mapping(target = "afterDiscount", source = "total")
//    @Mapping(target = "amount", source = "total")
//    @Mapping(target = "lineItem", source = "feeItem")
//    @Mapping(target = "student", source = "studentId")
//    FeesOrder toModel(PayVM lineItem);
//
//    List<FeesOrder> toModelList(List<PayVM> classDetails);
//
//    @Mapping(target = "paymentDateTime", source = "createdDate")
//    @Mapping(target = "total", source = "amount")
//    @Mapping(target = "payType", expression = "java(com.tulip.host.enums.PayTypeEnum.FEES)")
//    @Mapping(target = "studentId", source = "student.id")
//    @Mapping(target = "studentName", source = "student.name")
//    @Mapping(target = "feesItem", source = "lineItem")
//    @Mapping(target = "paymentReceivedBy", source = "createdBy")
//    PaySummaryDTO toEntity(FeesOrder feesOrder);
//
//    List<PaySummaryDTO> toEntityList(List<FeesOrder> feesOrder);
//}
