package com.tulip.host.mapper;

import com.tulip.host.domain.ClassDetail;
import com.tulip.host.domain.Employee;
import com.tulip.host.domain.Session;
import com.tulip.host.domain.SystemDocument;
import com.tulip.host.repository.ClassDetailRepository;
import com.tulip.host.repository.EmployeeRepository;
import com.tulip.host.repository.SessionRepository;
import com.tulip.host.web.rest.vm.DocumentVM;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UploadRecordMapper.class})
public abstract class DocumentMapper {

    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private ClassDetailRepository classDetailRepository;

    @Mapping(target = "classDetail", source = "std", qualifiedByName = "mapToClassDetail")
    @Mapping(target = "id", source = "id")
    public abstract SystemDocument toEntity(DocumentVM source);

    public abstract List<SystemDocument> toEntityList(List<DocumentVM> source);

    @Mapping(target = "session", source = "session.id")
    @Mapping(target = "std", source = "classDetail.id")
    @Mapping(target = "className", source = "classDetail.std")
    @Mapping(target = "id", source = "id")
    public abstract DocumentVM toDto(SystemDocument source);

    public abstract List<DocumentVM> toDtoList(List<SystemDocument> source);

    protected Session mapToSession(Long value) {
        if(value == null) {
            return null;
        }
        return sessionRepository.findById(value)
            .orElse(null);
    }


    @Named("mapToClassDetail")
    protected ClassDetail mapToClassDetail(Long value) {
        if (value == null) {
            return null;
        }
        return classDetailRepository.findById(value)
            .orElse(null);
    }

    @AfterMapping
    protected void setUploadsReference(DocumentVM vm , @MappingTarget SystemDocument systemDocument) {
        if (systemDocument.getFiles() != null) {
            systemDocument.getFiles().forEach(systemDoc -> systemDoc.setSystemDocument(systemDocument));
        }
    }


}
