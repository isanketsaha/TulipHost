package com.tulip.host.service;

import com.tulip.host.data.ClassDetailDTO;
import com.tulip.host.data.ClassListDTO;
import com.tulip.host.data.InventoryItemDTO;
import com.tulip.host.data.SessionDTO;
import com.tulip.host.data.StudentBasicDTO;
import com.tulip.host.domain.ClassDetail;
import com.tulip.host.domain.Student;
import com.tulip.host.mapper.ClassMapper;
import com.tulip.host.mapper.StudentMapper;
import com.tulip.host.repository.ClassDetailRepository;
import com.tulip.host.repository.StudentRepository;
import com.tulip.host.web.rest.vm.PromoteStudentVM;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class ClassroomService {

    private final ClassDetailRepository classDetailRepository;
    private final StudentRepository studentRepository;
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
    public List<ClassListDTO> fetchAllClasses(long sessionId) {
        if (sessionId == 0) {
            SessionDTO sessionDTO = sessionService.fetchCurrentSession();
            sessionId = sessionDTO.getId();
        }
        List<ClassDetail> allBySessionId = classDetailRepository.findAllBySessionId(sessionId);
        Collections.sort(allBySessionId, Comparator.comparing(ClassDetail::getStd));
        return classMapper.toClassListEntityList(allBySessionId);
    }

    @Transactional
    public void promoteStudents(PromoteStudentVM promoteStudentVM) {
        ClassDetail classDetail = classDetailRepository.findBySessionIdAndStd(
            promoteStudentVM.getSessionId(),
            promoteStudentVM.getStd().name()
        );
        if (classDetail != null) {
            promoteStudentVM
                .getStudentId()
                .stream()
                .forEach(item -> {
                    Student student = studentRepository.findById(item).orElse(null);
                    if (student != null) {
                        student.addClass(classDetail);
                        studentRepository.save(student);
                    }
                });
        }
    }

    public Long fetchClassDetails(String std, Long sessionId) {
        ClassDetail classDetail = classDetailRepository.findBySessionIdAndStd(sessionId, std);
        return classDetail.getId();
    }
}
