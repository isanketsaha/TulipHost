package com.tulip.host.mapper;

import com.tulip.host.data.ClassDetailDTO;
import com.tulip.host.data.ClassListDTO;
import com.tulip.host.domain.ClassDetail;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { StudentMapper.class, FeesCatalogMapper.class, ProductCatalogMapper.class })
public interface ClassMapper {
    @Mapping(target = "headTeacher", source = "headTeacher.name")
    @Mapping(target = "session", source = "session.displayText")
    ClassDetailDTO toEntity(ClassDetail classDetail);

    ClassListDTO toClassListEntity(ClassDetail classDetail);

    List<ClassListDTO> toClassListEntityList(List<ClassDetail> classDetails);
}
