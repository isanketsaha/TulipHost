package com.tulip.host.service;

import com.tulip.host.data.ClassSubjectDTO;
import com.tulip.host.data.CurriculumParametersDTO;
import com.tulip.host.domain.ClassDetail;
import com.tulip.host.domain.ClassSubject;
import com.tulip.host.domain.ProductCatalog;
import com.tulip.host.enums.ExamType;
import com.tulip.host.mapper.CurriculumMapper;
import com.tulip.host.mapper.ProductCatalogMapper;
import com.tulip.host.repository.AssessmentParameterRepository;
import com.tulip.host.repository.ClassDetailRepository;
import com.tulip.host.repository.ClassSubjectRepository;
import com.tulip.host.repository.ProductCatalogRepository;
import com.tulip.host.web.rest.vm.ClassSubjectVM;
import com.tulip.host.web.rest.vm.SaveCurriculumVM;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CurriculumService {

    private final ClassSubjectRepository classSubjectRepository;
    private final AssessmentParameterRepository assessmentParamRepo;
    private final ClassDetailRepository classDetailRepository;
    private final ProductCatalogRepository productCatalogRepository;
    private final CurriculumMapper curriculumMapper;
    private final ProductCatalogMapper productCatalogMapper;

    public CurriculumParametersDTO getParameters() {
        return new CurriculumParametersDTO(curriculumMapper.toAssessmentDTOList(assessmentParamRepo.findByActiveTrue()));
    }

    @Transactional(readOnly = true)
    public List<ClassSubjectDTO> getCurriculum(Long classroomId) {
        List<ClassSubject> subjects = classSubjectRepository.findAllByClassDetailId(classroomId);
        List<ClassSubjectDTO> dtos = curriculumMapper.toDTOList(subjects);
        for (int i = 0; i < subjects.size(); i++) {
            ClassSubject subject = subjects.get(i);
            List<ProductCatalog> books = productCatalogRepository.findBySubjectAndStd_Id(subject.getSubjectKey(), classroomId);
            dtos.get(i).setBooks(productCatalogMapper.toModelList(books));
        }
        return dtos;
    }

    @Transactional
    public List<ClassSubjectDTO> saveCurriculum(Long classroomId, SaveCurriculumVM vm) {
        ClassDetail classDetail = classDetailRepository
            .findById(classroomId)
            .orElseThrow(() -> new IllegalArgumentException("Classroom not found: " + classroomId));

        for (ClassSubjectVM subjectVM : vm.getSubjects()) {
            ClassSubject subject = classSubjectRepository
                .findByClassDetailIdAndSubjectKey(classroomId, subjectVM.getSubjectKey())
                .orElseGet(ClassSubject::new);

            subject.setClassDetail(classDetail);
            subject.setSubjectKey(subjectVM.getSubjectKey());
            subject.setDisplayName(subjectVM.getDisplayName());
            subject.setIncluded(subjectVM.isIncluded());
            Set<ExamType> types = subjectVM.getExamTypes() != null ? subjectVM.getExamTypes() : new HashSet<>();
            subject.setExamTypes(types);
            subject.setTermMarkType(types.contains(ExamType.TERM) ? subjectVM.getTermMarkType() : null);

            List<Long> assessmentIds = nullSafe(subjectVM.getAssessmentParamIds());
            subject.setAssessmentParams(new HashSet<>(assessmentParamRepo.findAllById(assessmentIds)));

            classSubjectRepository.save(subject);

            updateBookSubjects(subjectVM.getSubjectKey(), classroomId, nullSafe(subjectVM.getBookProductIds()));
        }

        return getCurriculum(classroomId);
    }

    private void updateBookSubjects(String subjectKey, Long classroomId, List<Long> bookProductIds) {
        List<ProductCatalog> stale = productCatalogRepository.findBySubjectAndStd_Id(subjectKey, classroomId);
        for (ProductCatalog p : stale) {
            if (!bookProductIds.contains(p.getId())) {
                p.setSubject(null);
                productCatalogRepository.save(p);
            }
        }
        if (!bookProductIds.isEmpty()) {
            List<ProductCatalog> books = productCatalogRepository.findAllById(bookProductIds);
            for (ProductCatalog book : books) {
                book.setSubject(subjectKey);
                productCatalogRepository.save(book);
            }
        }
    }

    private <T> List<T> nullSafe(List<T> list) {
        return list != null ? list : Collections.emptyList();
    }
}
