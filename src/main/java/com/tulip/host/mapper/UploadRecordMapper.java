package com.tulip.host.mapper;


import com.tulip.host.domain.UploadRecord;
import com.tulip.host.web.rest.vm.FileUploadVM;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UploadRecordMapper {

    UploadRecord toEntity(FileUploadVM source);

    List<UploadRecord> toEntityList(List<FileUploadVM> source);

    FileUploadVM toDto(UploadRecord source);

    List<FileUploadVM> toDtoList(List<UploadRecord> source);
}
