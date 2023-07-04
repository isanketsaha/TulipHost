package com.tulip.host.mapper;

import com.tulip.host.data.DuesDTO;
import com.tulip.host.domain.Dues;
import com.tulip.host.web.rest.vm.DueVM;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", uses = { DuesPaymentMapper.class })
public interface DuesMapper {
    @Mapping(target = "dueDate", source = "paymentDate")
    Dues toEntity(DueVM dueVM);

    DueVM toVm(Dues dues);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Dues partialUpdate(DueVM dueVM, @MappingTarget Dues dues);

    DuesDTO toDto(Dues dues);
}
