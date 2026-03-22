package com.tulip.host.mapper;

import com.tulip.host.data.InventoryRequestDTO;
import com.tulip.host.domain.InventoryRequest;
import java.time.format.DateTimeFormatter;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InventoryRequestMapper {
    @Mapping(target = "employeeId", source = "employee.id")
    @Mapping(target = "employeeName", source = "employee.name")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.itemName")
    @Mapping(target = "productCategory", source = "product.category")
    @Mapping(target = "createdDate", ignore = true)
    InventoryRequestDTO toDto(InventoryRequest inventoryRequest);

    @AfterMapping
    default void mapCreatedDate(InventoryRequest source, @MappingTarget InventoryRequestDTO target) {
        if (source.getCreatedDate() != null) {
            target.setCreatedDate(source.getCreatedDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")));
        }
    }
}
