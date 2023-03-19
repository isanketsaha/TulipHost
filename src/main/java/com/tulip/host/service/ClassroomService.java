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
import com.tulip.host.utils.CommonUtils;
import com.tulip.host.web.rest.errors.BadRequestAlertException;
import com.tulip.host.web.rest.errors.BusinessValidationException;
import com.tulip.host.web.rest.vm.PromoteStudentVM;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.xml.bind.ValidationException;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
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

    private final EntityManager em;

    @Transactional
    public ClassDetailDTO fetchClassDetails(Long classroomId) {
        Session unwrap = em.unwrap(Session.class);
        unwrap.enableFilter("filterTransactionOnType").setParameter("type", "FEES");
        unwrap.enableFilter("filterClass").setParameter("classId", classroomId);
        ClassDetail classDetail = classDetailRepository.findByClass(classroomId);
        if (classDetail != null) {
            ClassDetailDTO classDetailDTO = classMapper.toEntity(classDetail);
            classDetailDTO.getStudents().sort((s1, s2) -> s1.getName().toUpperCase().compareTo(s2.getName().toUpperCase()));
            unwrap.disableFilter("filterTransactionOnType");
            unwrap.disableFilter("filterClass");
            return classDetailDTO;
        }
        return null;
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
    public void promoteStudents(PromoteStudentVM promoteStudentVM) throws ValidationException {
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
                        Set<ClassDetail> classDetails = student
                            .getClassDetails()
                            .stream()
                            .filter(classDetail1 -> classDetail1.getSession().getId() != promoteStudentVM.getSessionId())
                            .collect(Collectors.toSet());
                        classDetails.add(classDetail);
                        student.setClassDetails(classDetails);
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
