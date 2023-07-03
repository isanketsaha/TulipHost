package com.tulip.host.mapper;

import com.tulip.host.data.DuesPaymentDTO;
import com.tulip.host.domain.DuesPayment;
import com.tulip.host.web.rest.vm.DuePaymentVm;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface DuesPaymentMapper {
    DuesPayment toEntity(DuesPaymentDTO duesPaymentDTO);

    DuesPaymentDTO toDto(DuesPayment duesPayment);

    DuesPayment toEntity(DuePaymentVm paymentVm);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    DuesPayment partialUpdate(DuesPaymentDTO duesPaymentDTO, @MappingTarget DuesPayment duesPayment);
}
