package com.tulip.host.mapper;

import com.tulip.host.data.FeesItemSummaryDTO;
import com.tulip.host.data.StudentDetailsDTO;
import com.tulip.host.domain.FeesCatalog;
import com.tulip.host.domain.FeesLineItem;
import com.tulip.host.domain.Student;
import com.tulip.host.domain.TransportCatalog;
import com.tulip.host.enums.FeesRuleType;
import com.tulip.host.repository.impl.ReferenceMapper;
import com.tulip.host.service.UploadService;
import com.tulip.host.web.rest.vm.FeePayVM;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = { FeesCatalogMapper.class, ReferenceMapper.class })
public interface FeeLineItemMapper {
    @Mapping(target = "feesProduct", source = "feesId", conditionExpression = "java(lineItem.getType().equals(\"CLASS_FEES\"))")
    @Mapping(target = "transport", source = "feesId", conditionExpression = "java(lineItem.getType().equals(\"TRANSPORT_FEES\"))")
    FeesLineItem toModel(FeePayVM lineItem);

    Set<FeesLineItem> toModelList(List<FeePayVM> classDetails);

    @Mapping(target = "feesTitle", ignore = true)
    @Mapping(target = "feesId", ignore = true)
    @Mapping(target = "itemId", source = "id")
    @Mapping(target = "applicableRule", source = "feesProduct.applicableRule")
    FeesItemSummaryDTO toEntity(FeesLineItem feesOrder);

    List<FeesItemSummaryDTO> toEntityList(Set<FeesLineItem> classDetails);

    @AfterMapping
    default void map(@MappingTarget FeesItemSummaryDTO target, FeesLineItem source) {
        if (source.getFeesProduct() != null) {
            FeesCatalog feesProduct = source.getFeesProduct();
            target.setFeesTitle(feesProduct.getFeesName());
            target.setFeesId(feesProduct.getId());
        } else if (source.getTransport() != null) {
            TransportCatalog transport = source.getTransport();
            target.setApplicableRule(FeesRuleType.MONTHLY.name());
            target.setFeesTitle("Transport Fees - " + transport.getLocation());
            target.setFeesId(transport.getId());
        }
    }
}
