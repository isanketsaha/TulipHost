package com.tulip.host.service;

import com.mysql.cj.xdevapi.SessionFactory;
import com.querydsl.jpa.hibernate.HibernateUtil;
import com.tulip.host.data.ClassDetailDTO;
import com.tulip.host.data.ClassListDTO;
import com.tulip.host.data.SessionDTO;
import com.tulip.host.domain.ClassDetail;
import com.tulip.host.domain.FeesLineItem;
import com.tulip.host.domain.Student;
import com.tulip.host.mapper.ClassMapper;
import com.tulip.host.mapper.StudentMapper;
import com.tulip.host.repository.ClassDetailRepository;
import com.tulip.host.repository.FeesLineItemRepository;
import com.tulip.host.repository.StudentRepository;
import com.tulip.host.repository.TransactionRepository;
import com.tulip.host.web.rest.vm.PromoteStudentVM;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.xml.bind.ValidationException;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClassroomService {

    private final ClassDetailRepository classDetailRepository;

    private final StudentRepository studentRepository;
    private final ClassMapper classMapper;

    private final StudentMapper studentMapper;

    private final StudentService studentService;

    private final SessionService sessionService;

    private final TransactionRepository transactionRepository;

    private final EntityManager em;

    @Transactional
    public ClassDetailDTO fetchClassDetails(Long classroomId) {
        Session unwrap = em.unwrap(Session.class);
        unwrap.enableFilter("filterClass").setParameter("classId", classroomId);
        unwrap.enableFilter("filterCatalogNEPlaceholder");
        unwrap.enableFilter("activeStudent").setParameter("flag", true);

        ClassDetail classDetail = classDetailRepository.findByClass(classroomId);
        if (classDetail != null) {
            ClassDetailDTO classDetailDTO = classMapper.toEntity(classDetail);
            classDetailDTO
                .getStudents()
                .forEach(item -> {
                    item.setPendingFees(
                        studentService.calculatePendingMonthFees(item, classDetail.getId(), classDetail.getSession().getFromDate())
                    );
                    item.setAnnualPaidFees(transactionRepository.fetchAnnualFeesByClass(item.getId(), classroomId));
                });
            classDetailDTO.getStudents().sort((s1, s2) -> s1.getName().toUpperCase().compareTo(s2.getName().toUpperCase()));
            unwrap.disableFilter("filterClass");
            unwrap.disableFilter("filterCatalogNEPlaceholder");
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
                        SortedSet<ClassDetail> classDetails = student
                            .getClassDetails()
                            .stream()
                            .filter(classDetail1 -> classDetail1.getSession().getId() != promoteStudentVM.getSessionId())
                            .collect(Collectors.toCollection(TreeSet::new));
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
