package com.tulip.host.service;

import com.tulip.host.data.ClassDetailDTO;
import com.tulip.host.data.ClassListDTO;
import com.tulip.host.data.StudentBasicDTO;
import com.tulip.host.domain.ClassDetail;
import com.tulip.host.mapper.ClassMapper;
import com.tulip.host.mapper.StudentMapper;
import com.tulip.host.repository.ClassDetailRepository;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class ClassroomService {

    private final ClassDetailRepository classDetailRepository;

    private final ClassMapper classMapper;

    private final StudentMapper studentMapper;

    @Transactional
    public ClassDetailDTO fetchClassDetails(Long classroomId) {
        ClassDetail classDetail = classDetailRepository.findById(classroomId).orElse(null);
        return classMapper.toEntity(classDetail);
    }

    @Transactional
    public List<ClassListDTO> fetchAllClasses(Long sessionId) {
        List<ClassDetail> allBySessionId = classDetailRepository.findAllBySessionId(sessionId);
        return classMapper.toClassListEntityList(allBySessionId);
    }
}
