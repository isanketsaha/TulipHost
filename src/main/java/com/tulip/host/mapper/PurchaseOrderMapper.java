//package com.tulip.host.mapper;
//
//import com.tulip.host.data.PaySummaryDTO;
//import com.tulip.host.web.rest.vm.PayVM;
//import java.util.List;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//
//@Mapper(componentModel = "spring", uses = { PurchaseLineItemMapper.class, StudentMapper.class })
//public interface PurchaseOrderMapper {
//    @Mapping(target = "afterDiscount", source = "total")
//    @Mapping(target = "amount", source = "total")
//    @Mapping(target = "lineItem", source = "purchaseItems")
//    @Mapping(target = "student", source = "studentId")
//    @Mapping(target = "discount", expression = "java(0)")
//    PurchaseOrder toModel(PayVM lineItem);
//
//    List<PurchaseOrder> toModelList(List<PayVM> classDetails);
//
//    @Mapping(target = "paymentDateTime", source = "createdDate")
//    @Mapping(target = "total", source = "amount")
//    @Mapping(target = "payType", expression = "java(com.tulip.host.enums.PayTypeEnum.PURCHASE)")
//    @Mapping(target = "studentId", source = "student.id")
//    @Mapping(target = "studentName", source = "student.name")
//    @Mapping(target = "purchaseItems", source = "lineItem")
//    @Mapping(target = "paymentReceivedBy", source = "createdBy")
//    PaySummaryDTO toEntity(PurchaseOrder purchaseOrder);
//
//    List<PaySummaryDTO> toEntityList(List<PurchaseOrder> purchaseOrderList);
//}
