package com.tulip.host.mapper;

import com.tulip.host.domain.Upload;
import com.tulip.host.web.rest.vm.FileUploadVM;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UploadMapper {
    Upload toModel(FileUploadVM uploadVM);

    Set<Upload> toModelList(List<FileUploadVM> uploadVM);

    FileUploadVM toEntity(Upload uploadMapper);

    List<FileUploadVM> toEntityList(Set<Upload> uploadMapper);

    List<FileUploadVM> toEntityList(List<Upload> uploadMapper);

    default List<FileUploadVM> map(Upload value) {
        return Arrays.asList(toEntity(value));
    }
}
