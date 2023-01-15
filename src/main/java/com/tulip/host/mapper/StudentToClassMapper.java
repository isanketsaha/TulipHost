//package com.tulip.host.mapper;
//
//import com.tulip.host.domain.StudentToClass;
//import com.tulip.host.domain.UserToDependent;
//import com.tulip.host.web.rest.vm.DependentVM;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//
//import java.util.List;
//
//@Mapper(componentModel = "spring")
//public interface StudentToClassMapper {
//
//    @Mapping(target = "dependent", source = ".")
//    StudentToClass toModel(DependentVM dependentVM);
//
//    StudentToClass toEntity(Long id);
//
//
//}
