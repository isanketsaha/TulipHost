package com.tulip.host.mapper;

import com.tulip.host.domain.Upload;
import com.tulip.host.web.rest.vm.UploadVM;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UploadMapper {
    Upload toModel(UploadVM uploadVM);

    Set<Upload> toModelList(List<UploadVM> uploadVM);

    UploadVM toEntity(Upload uploadMapper);

    List<UploadVM> toEntityList(Set<Upload> uploadMapper);
}
