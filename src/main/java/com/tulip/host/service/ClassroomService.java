package com.tulip.host.service;

import com.tulip.host.data.ClassDetailDTO;
import com.tulip.host.data.ClassListDTO;
import com.tulip.host.data.SessionDTO;
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

    private final SessionService sessionService;

    @Transactional
    public ClassDetailDTO fetchClassDetails(Long classroomId) {
        ClassDetail classDetail = classDetailRepository.findById(classroomId).orElse(null);
        ClassDetailDTO classDetailDTO = classMapper.toEntity(classDetail);
        classDetailDTO.getStudents().sort((s1, s2) -> s1.getName().toUpperCase().compareTo(s2.getName().toUpperCase()));
        return classDetailDTO;
    }

    @Transactional
    public List<ClassListDTO> fetchAllClasses() {
        SessionDTO sessionDTO = sessionService.fetchCurrentSession();
        List<ClassDetail> allBySessionId = classDetailRepository.findAllBySessionId(sessionDTO.getId());
        return classMapper.toClassListEntityList(allBySessionId);
    }
}
