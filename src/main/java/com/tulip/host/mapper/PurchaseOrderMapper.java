package com.tulip.host.mapper;

import com.tulip.host.domain.FeesOrder;
import com.tulip.host.domain.PurchaseOrder;
import com.tulip.host.web.rest.vm.PayVM;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { PurchaseLineItemMapper.class, StudentMapper.class })
public interface PurchaseOrderMapper {
    @Mapping(target = "afterDiscount", source = "total")
    @Mapping(target = "amount", source = "total")
    @Mapping(target = "lineItem", source = "purchaseItems")
    @Mapping(target = "student", source = "studentId")
    @Mapping(target = "discount", expression = "java(0)")
    PurchaseOrder toModel(PayVM lineItem);

    List<PurchaseOrder> toModelList(List<PayVM> classDetails);
}
