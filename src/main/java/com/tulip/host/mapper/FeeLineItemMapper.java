package com.tulip.host.mapper;

import com.tulip.host.data.FeesItemSummaryDTO;
import com.tulip.host.data.PaySummaryDTO;
import com.tulip.host.domain.FeesLineItem;
import com.tulip.host.domain.FeesOrder;
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
    FeesLineItem toModel(FeePayVM lineItem);

    Set<FeesLineItem> toModelList(List<FeePayVM> classDetails);

    @Mapping(source = "fromMonth", target = "from")
    @Mapping(source = "toMonth", target = "to")
    @Mapping(target = "feesTitle", source = "feesProduct.feesName")
    @Mapping(target = "feesId", source = "feesProduct.id")
    FeesItemSummaryDTO toEntity(FeesLineItem feesOrder);

    List<FeesItemSummaryDTO> toEntityList(Set<FeesLineItem> classDetails);
}
