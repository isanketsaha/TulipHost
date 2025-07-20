package com.tulip.host.mapper;

import com.tulip.host.data.TransportOptDTO;
import com.tulip.host.domain.StudentToTransport;
import com.tulip.host.repository.impl.ReferenceMapper;
import com.tulip.host.web.rest.vm.TransportVm;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { ReferenceMapper.class, TransportCatalogMapper.class })
public interface StudentToTransportMapper {
    @Mapping(source = "locationId", target = "transport")
    StudentToTransport toEntity(TransportVm vm);

    @Mapping(target = "location", source = "transport.location")
    @Mapping(target = "amount", source = "transport.amount")
    @Mapping(target = "id", source = "transport.id")
    TransportOptDTO fromEntity(StudentToTransport transportList);

    default TransportOptDTO fromEntityList(Set<StudentToTransport> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            Optional<StudentToTransport> studentToTransport = list
                .stream()
                    .filter(item -> item
                            .getEndDate() == null
                )
                .findFirst();
            if (studentToTransport.isPresent()) return fromEntity(studentToTransport.orElseThrow());
        }
        return null;
    }
}
