package com.tulip.host.mapper;

import com.tulip.host.data.StudentBasicDTO;
import com.tulip.host.domain.Student;
import com.tulip.host.domain.UserToDependent;
import com.tulip.host.web.rest.vm.DependentVM;
import com.tulip.host.web.rest.vm.OnboardingVM;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { DependentMapper.class })
public interface UserToDependentMapper {
    @Mapping(target = "dependent", source = ".")
    UserToDependent toModel(DependentVM dependentVM);

    UserToDependent toEntity(Long id);

    List<UserToDependent> toModelList(List<DependentVM> list);
}
