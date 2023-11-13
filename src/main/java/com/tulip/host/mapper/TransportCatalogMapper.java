package com.tulip.host.mapper;

import com.tulip.host.data.TransportCatalogDto;
import com.tulip.host.domain.TransportCatalog;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransportCatalogMapper {
    TransportCatalogDto toDto(TransportCatalog transportCatalog);
    List<TransportCatalogDto> toDtoList(List<TransportCatalog> transportCatalog);
}
