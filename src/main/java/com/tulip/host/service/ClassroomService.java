package com.tulip.host.service;

import com.tulip.host.data.ClassDetailDTO;
import com.tulip.host.data.StudentBasicDTO;
import com.tulip.host.domain.ClassDetail;
import com.tulip.host.mapper.ClassMapper;
import com.tulip.host.mapper.StudentMapper;
import com.tulip.host.repository.ClassDetailRepository;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class ClassroomService {

    private final ClassDetailRepository classDetailRepository;

    private final ClassMapper classMapper;

    private final StudentMapper studentMapper;

    public List<ClassDetailDTO> fetchAllClassroom(Long sessionId) {
        List<ClassDetail> allBySessionId = classDetailRepository.findAllBySessionId(sessionId);
        if (!CollectionUtils.isEmpty(allBySessionId)) {
            return classMapper.toEntityList(allBySessionId);
        }
        return Collections.EMPTY_LIST;
    }

    public List<StudentBasicDTO> fetchStudentList(Long classroomId) {
        ClassDetail classDetail = classDetailRepository.findById(classroomId).orElse(null);
        if (classDetail != null) {
            return studentMapper.toBasicEntityList(classDetail.getStudents());
        }
        return Collections.emptyList();
    }
}
