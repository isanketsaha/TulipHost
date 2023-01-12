package com.tulip.host.mapper;

import com.tulip.host.domain.FeesLineItem;
import com.tulip.host.domain.FeesOrder;
import com.tulip.host.web.rest.vm.PayVM;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = { FeeLineItemMapper.class, StudentMapper.class })
public interface FeesOrderMapper {
    @Mapping(target = "afterDiscount", source = "total")
    @Mapping(target = "amount", source = "total")
    @Mapping(target = "lineItem", source = "feeItem")
    @Mapping(target = "student", source = "studentId")
    FeesOrder getModelFromEntity(PayVM lineItem);

    List<FeesOrder> getModelListFromEntityList(List<PayVM> classDetails);
}
