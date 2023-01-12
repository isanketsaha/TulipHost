package com.tulip.host.mapper;

import com.tulip.host.domain.FeesLineItem;
import com.tulip.host.web.rest.vm.FeePayVM;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { FeesCatalogMapper.class })
public interface FeeLineItemMapper {
    @Mapping(target = "fromMonth", source = "from")
    @Mapping(target = "toMonth", source = "to")
    @Mapping(target = "feesProduct", source = "feesId")
    FeesLineItem getModelFromEntity(FeePayVM lineItem);

    Set<FeesLineItem> getModelListFromEntityList(List<FeePayVM> classDetails);
}
