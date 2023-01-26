package com.tulip.host.mapper;

import com.tulip.host.data.ClassDetailDTO;
import com.tulip.host.domain.ClassDetail;
import com.tulip.host.domain.Credential;
import com.tulip.host.web.rest.vm.CredentialVM;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = PasswordEncoderMapper.class)
public interface CredentialMapper {
    @Mapping(target = "password", qualifiedBy = EncodedMapping.class)
    Credential toEntity(CredentialVM vm);
}
