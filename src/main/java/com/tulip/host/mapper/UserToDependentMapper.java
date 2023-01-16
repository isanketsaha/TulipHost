package com.tulip.host.mapper;

import com.tulip.host.data.DependentDTO;
import com.tulip.host.domain.UserToDependent;
import com.tulip.host.web.rest.vm.DependentVM;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { DependentMapper.class })
public interface UserToDependentMapper {
    @Mapping(target = "dependent", source = ".")
    UserToDependent toModel(DependentVM dependentVM);

    UserToDependent toEntity(Long id);

    List<UserToDependent> toModelList(List<DependentVM> list);

    List<DependentDTO> toStudentModel(List<UserToDependent> dependents);

    @Mapping(target = ".", source = "student")
    DependentDTO map(UserToDependent userToDependent);
}
