package com.tulip.host.mapper;

import com.tulip.host.data.BankDTO;
import com.tulip.host.domain.Bank;
import com.tulip.host.domain.Employee;
import com.tulip.host.domain.Interview;
import com.tulip.host.web.rest.vm.BankVM;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BankMapper {
    @Mapping(target = "accountNumber", source = "accountNo")
    BankDTO getEntityFromModel(Bank source);

    Bank getModelFromEntity(BankVM source);

    //    Employee toModel(List<BankVM> source);

    default Employee map(List<BankVM> source) {
        return map(source.get(0));
    }

    @Mapping(target = "bank", source = ".")
    Employee map(BankVM source);
}
