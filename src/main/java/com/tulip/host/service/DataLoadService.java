package com.tulip.host.service;

import com.tulip.host.domain.ClassDetail;
import com.tulip.host.domain.Dependent;
import com.tulip.host.domain.FeesCatalog;
import com.tulip.host.domain.Inventory;
import com.tulip.host.domain.ProductCatalog;
import com.tulip.host.domain.Session;
import com.tulip.host.domain.Student;
import com.tulip.host.enums.RelationEnum;
import com.tulip.host.mapper.ClassMapper;
import com.tulip.host.mapper.FeesCatalogMapper;
import com.tulip.host.mapper.InventoryMapper;
import com.tulip.host.mapper.ProductCatalogMapper;
import com.tulip.host.mapper.SessionMapper;
import com.tulip.host.mapper.StudentMapper;
import com.tulip.host.repository.ClassDetailRepository;
import com.tulip.host.repository.EmployeeRepository;
import com.tulip.host.repository.FeesCatalogRepository;
import com.tulip.host.repository.InventoryRepository;
import com.tulip.host.repository.ProductCatalogRepository;
import com.tulip.host.repository.SessionRepository;
import com.tulip.host.repository.StudentRepository;
import com.tulip.host.web.rest.vm.FeesLoadVM;
import com.tulip.host.web.rest.vm.ProductLoadVM;
import com.tulip.host.web.rest.vm.SessionLoadVM;
import com.tulip.host.web.rest.vm.StudentLoadVm;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.WordUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataLoadService {

    private final EmployeeRepository employeeRepository;
    private final SessionRepository sessionRepository;

    private final StudentMapper studentMapper;

    private final ClassDetailRepository classDetailRepository;

    private final StudentRepository studentRepository;

    private final FeesCatalogMapper feesCatalogMapper;
    private final FeesCatalogRepository feesCatalogRepository;

    private final ProductCatalogMapper productCatalogMapper;
    private final InventoryMapper inventoryMapper;

    private final InventoryRepository inventoryRepository;

    private final SessionMapper sessionMapper;
    private final ClassMapper classMapper;

    @Transactional
    public void loadStudents(List<StudentLoadVm> list) {
        List<Student> studentList = list
            .stream()
            .map(item -> {
                ClassDetail classDetail = classDetailRepository.findBySessionIdAndStd(item.getSession(), item.getStd());
                Student student = studentMapper.toModel(item);
                student.addClass(classDetail);
                Dependent dependent = Dependent
                    .builder()
                    .name(WordUtils.capitalizeFully(item.getFatherName()))
                    .aadhaarNo(item.getFatherAadhaar())
                    .relationship(RelationEnum.FATHER.name())
                    .qualification(item.getFatherQualification())
                    .contact(item.getFatherContact())
                    .occupation(item.getFatherOccupation())
                    .build();
                student.addDependent(dependent);
                return student;
            })
            .collect(Collectors.toList());
        try {
            studentRepository.saveAllAndFlush(studentList);
        } catch (DataIntegrityViolationException e) {
            log.error("{}", e);
        }
        log.info("Successfully loaded data");
    }

    @Transactional
    public void loadFees(List<FeesLoadVM> fees) {
        List<FeesCatalog> feesCatalogList = fees
            .stream()
            .map(item -> {
                item.setRule(item.getRule().toUpperCase());
                //                ClassDetail classDetail = classDetailRepository.findBySessionIdAndStd(item.getSession(), item.getClassDetail());
                FeesCatalog feesCatalog = feesCatalogMapper.toModel(item);
                //                feesCatalog.setStd(classDetail);
                return feesCatalog;
            })
            .collect(Collectors.toList());
        feesCatalogRepository.saveAllAndFlush(feesCatalogList);
    }

    public void getFees() {}

    @Transactional
    public void loadSession(SessionLoadVM loadVM) {
        Session session = sessionMapper.toModel(loadVM);
        session.setStdList(
            loadVM
                .getStdList()
                .stream()
                .map(std -> {
                    return ClassDetail
                        .builder()
                        .std(std.getStd())
                        .headTeacher(employeeRepository.findById(std.getClassTeacher()).orElseThrow())
                        .session(session)
                        .build();
                })
                .collect(Collectors.toList())
        );
        session
            .getStdList()
            .forEach(item -> {
                Set<FeesCatalog> feesCatalogs = feesCatalogMapper
                    .toModelList(loadVM.getFeesList().get(item.getStd()))
                    .stream()
                    .peek(ele -> ele.setStd(item))
                    .collect(Collectors.toSet());
                item.setFeesCatalogs(feesCatalogs);
            });
        sessionRepository.save(session);
    }

    public void removeSession(Long id) {
        sessionRepository.deleteById(id);
    }
}
