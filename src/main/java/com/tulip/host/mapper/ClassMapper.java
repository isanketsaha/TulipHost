package com.tulip.host.mapper;

import com.tulip.host.data.ClassDetailDTO;
import com.tulip.host.data.ClassListDTO;
import com.tulip.host.domain.ClassDetail;
import com.tulip.host.domain.FeesCatalog;
import com.tulip.host.repository.impl.ReferenceMapper;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = { StudentMapper.class, FeesCatalogMapper.class, ProductCatalogMapper.class, ReferenceMapper.class }
)
public interface ClassMapper {
    @Mapping(target = "headTeacher", source = "headTeacher.name")
    @Mapping(target = "session", source = "session.displayText")
    ClassDetailDTO toEntity(ClassDetail classDetail);

    ClassListDTO toClassListEntity(ClassDetail classDetail);

    List<ClassListDTO> toClassListEntityList(List<ClassDetail> classDetails);

    List<ClassDetailDTO> toClassDetailList(Set<ClassDetail> classDetails);

    ClassDetail toModel(Long id);
}
