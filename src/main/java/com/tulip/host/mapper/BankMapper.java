package com.tulip.host.mapper;

import com.tulip.host.data.BankDTO;
import com.tulip.host.domain.Bank;
import com.tulip.host.domain.Employee;
import com.tulip.host.web.rest.vm.BankVM;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BankMapper {
    @Mapping(target = "accountNumber", source = "accountNo")
    BankDTO toEntity(Bank source);

    @Mapping(target = "accountNo", source = "accountNumber")
    Bank toModel(BankVM source);
}
