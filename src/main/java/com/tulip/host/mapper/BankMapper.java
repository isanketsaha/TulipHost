package com.tulip.host.mapper;

import com.tulip.host.data.BankDTO;
import com.tulip.host.domain.Bank;
import com.tulip.host.web.rest.vm.BankVM;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BankMapper {
    BankMapper INSTANCE = Mappers.getMapper(BankMapper.class);

    @Mapping(target = "accountNumber", source = "accountNo")
    BankDTO getEntityFromModel(Bank source);

    Bank getModelFromEntity(BankVM source);
}
