package com.tulip.host.mapper;

import com.tulip.host.data.ClassDetailDTO;
import com.tulip.host.data.ClassListDTO;
import com.tulip.host.data.StudentDetailsDTO;
import com.tulip.host.domain.ClassDetail;
import com.tulip.host.domain.FeesCatalog;
import com.tulip.host.domain.Student;
import com.tulip.host.repository.EmployeeRepository;
import com.tulip.host.repository.impl.ReferenceMapper;
import com.tulip.host.service.UploadService;
import com.tulip.host.web.rest.vm.ClassLoadVM;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = { StudentMapper.class, FeesCatalogMapper.class, ProductCatalogMapper.class })
public interface ClassMapper {
    @Mapping(target = "headTeacher", source = "headTeacher.name")
    @Mapping(target = "session", source = "session.displayText")
    @Mapping(target = "sessionId", source = "session.id")
    ClassDetailDTO toEntity(ClassDetail classDetail);

    @Mapping(target = "studentStrength", expression = "java(classDetail.getStudents().size())")
    ClassListDTO toClassListEntity(ClassDetail classDetail);

    List<ClassListDTO> toClassListEntityList(List<ClassDetail> classDetails);

    List<ClassDetailDTO> toClassDetailList(Set<ClassDetail> classDetails);
}
