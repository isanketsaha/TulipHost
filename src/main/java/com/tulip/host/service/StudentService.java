package com.tulip.host.service;

import static com.tulip.host.config.Constants.AADHAAR_CARD;
import static com.tulip.host.config.Constants.BIRTH_CERTIFICATE;
import static com.tulip.host.config.Constants.MONTH_YEAR_FORMAT;
import static com.tulip.host.config.Constants.PAN_CARD;
import static com.tulip.host.utils.CommonUtils.isDateAfterCurrent;

import com.querydsl.core.BooleanBuilder;
import com.tulip.host.data.ClassDetailDTO;
import com.tulip.host.data.StudentBasicDTO;
import com.tulip.host.data.StudentDetailsDTO;
import com.tulip.host.domain.ClassDetail;
import com.tulip.host.domain.Dependent;
import com.tulip.host.domain.QStudent;
import com.tulip.host.domain.Session;
import com.tulip.host.domain.Student;
import com.tulip.host.domain.StudentToTransport;
import com.tulip.host.domain.StudentToTransportId;
import com.tulip.host.domain.Transaction;
import com.tulip.host.exceptions.ResourceNotFoundException;
import com.tulip.host.mapper.ClassMapper;
import com.tulip.host.mapper.DependentMapper;
import com.tulip.host.mapper.StudentMapper;
import com.tulip.host.mapper.StudentToTransportMapper;
import com.tulip.host.mapper.UploadMapper;
import com.tulip.host.repository.ClassDetailRepository;
import com.tulip.host.repository.DependentRepository;
import com.tulip.host.repository.StudentPagedRepository;
import com.tulip.host.repository.StudentRepository;
import com.tulip.host.repository.StudentToTransportRepository;
import com.tulip.host.repository.TransactionRepository;
import com.tulip.host.utils.CommonUtils;
import com.tulip.host.web.rest.vm.DeactivateVm;
import com.tulip.host.web.rest.vm.OnboardingVM;
import com.tulip.host.web.rest.vm.TransportVm;
import com.tulip.host.web.rest.vm.UserEditVM;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudentService {

    private final StudentToTransportRepository studentToTransportRepository;

    private final StudentRepository studentRepository;
    private final ClassDetailRepository classDetailRepository;
    private final StudentMapper studentMapper;
    private final UploadService uploadService;
    private final ClassMapper classMapper;
    private final DependentMapper dependentMapper;
    private final UploadMapper uploadMapper;
    private final StudentPagedRepository studentPagedRepository;
    private final DependentRepository dependentRepository;

    private final TransactionRepository transactionRepository;

    private final StudentToTransportMapper studentToTransportMapper;

    @Transactional
    public Page<StudentBasicDTO> fetchAllStudent(int pageNo, int pageSize) {
        Page<Student> students = studentPagedRepository.findAll(
            new BooleanBuilder().and(QStudent.student.active.eq(true)),
            CommonUtils.getPageRequest(Sort.Direction.DESC, "createdDate", pageNo, pageSize)
        );
        List<StudentBasicDTO> studentBasicDTOS = studentMapper.toBasicEntityList(students.getContent());
        return new PageImpl<>(studentBasicDTOS, students.getPageable(), students.getTotalElements());
    }

    @Transactional
    public Long addStudent(OnboardingVM onboardingVM) {
        ClassDetail classDetail = classDetailRepository.findBySessionIdAndStd(onboardingVM.getSession(), onboardingVM.getStd()
            .name());
        Student student = studentMapper.toModel(onboardingVM);
        student.addClass(classDetail);
        addUpload(student, onboardingVM);
        Student save = studentRepository.save(student);
        return save.getId();
    }

    @Transactional
    public List<StudentBasicDTO> searchStudent(String name) {
        List<Student> studentList = studentRepository.search(name);
        return studentMapper.toBasicEntityList(studentList);
    }

    @Transactional
    public StudentDetailsDTO searchStudent(long id) {
        Student byId = studentRepository.search(id);
        if (byId != null && !CollectionUtils.isEmpty(byId.getClassDetails())) {
            List<ClassDetailDTO> classDetailDTOS = classMapper.toClassDetailList(byId.getClassDetails());
            StudentDetailsDTO studentDetailsDTO = studentMapper.toDetailEntity(byId, uploadService);
            mapUpload(studentDetailsDTO, byId);
            studentDetailsDTO.setClassDetails(classDetailDTOS);
            return studentDetailsDTO;
        }
        return null;
    }

    private void mapUpload(StudentDetailsDTO studentDetailsDTO, Student byId) {
        if (byId.getProfilePicture() != null) studentDetailsDTO.setProfilePicture(
            Arrays.asList(uploadMapper.toEntity(byId.getProfilePicture()))
        );
        byId
            .getUploadedDocuments()
            .forEach(item -> {
                if (item.getDocumentType()
                    .equals(AADHAAR_CARD)) {
                    studentDetailsDTO.getAadhaarCard()
                        .add(uploadMapper.toEntity(item));
                } else if (item.getDocumentType()
                    .equals(BIRTH_CERTIFICATE)) {
                    studentDetailsDTO.getBirthCertificate()
                        .add(uploadMapper.toEntity(item));
                } else if (item.getDocumentType()
                    .equals(PAN_CARD)) {
                    studentDetailsDTO.getPanCard()
                        .add(uploadMapper.toEntity(item));
                }
            });
    }

    @Transactional
    public StudentBasicDTO basicSearchStudent(long id) {
        Student byId = studentRepository.findById(id)
            .orElse(null);
        if (byId != null) {
            return studentMapper.toBasicEntity(byId);
        }
        return null;
    }

    @Transactional
    public StudentBasicDTO basicSearchStudent(long id, long classId) {
        Student byId = studentRepository.searchByClassId(id, classId);
        if (byId != null) {
            return studentMapper.toBasicEntity(byId);
        }
        return null;
    }

    @Transactional
    public void editStudentDetails(UserEditVM editVM) {
        Student byId = studentRepository.findById(editVM.getId())
            .orElse(null);
        if (byId != null) {
            studentMapper.toUpdateModel(editVM, byId);
            if (editVM.getAadhaarCard() != null) {
                byId.addUpload(uploadMapper.toModelList(editVM.getAadhaarCard()));
            }
            if (editVM.getPanCard() != null) {
                byId.addUpload(uploadMapper.toModelList(editVM.getPanCard()));
            }
            if (editVM.getBirthCertificate() != null) {
                byId.addUpload(uploadMapper.toModelList(editVM.getBirthCertificate()));
            }
            studentRepository.saveAndFlush(byId);
            if (editVM.getDependent() != null) {
                editVM
                    .getDependent()
                    .forEach(dependentVM -> {
                        Dependent dependent = dependentRepository.findById(dependentVM.getId())
                            .orElse(null);

                        dependentMapper.toUpdateModel(dependentVM, dependent);
                        if (dependentVM.getAadhaarCard() != null) {
                            dependent.getUploadedDocuments()
                                .forEach(item -> item.setDependent(dependent));
                        }
                        dependentRepository.saveAndFlush(dependent);
                    });
            }
        }
    }

    public void deactivate(DeactivateVm vm) {
        Student byId = studentRepository.findById(vm.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        byId.setActive(Boolean.FALSE);
        byId.setTerminationDate(LocalDateTime.now());
        byId.setTc(vm.getTc());
        byId.setTerminationReason(vm.getReason());
        studentRepository.save(byId);
    }

    public void addUpload(Student student, OnboardingVM onboardingVM) {
        if (onboardingVM.getAadhaarCard() != null) {
            student.addUpload(uploadMapper.toModelList(onboardingVM.getAadhaarCard()));
        }
        if (onboardingVM.getBirthCertificate() != null) {
            student.addUpload(uploadMapper.toModelList(onboardingVM.getBirthCertificate()));
        }
        student
            .getDependents()
            .stream()
            .filter(item -> !CollectionUtils.isEmpty(item.getUploadedDocuments()))
            .forEach(item -> item.getUploadedDocuments()
                .forEach(upload -> upload.setDependent(item)));
    }

    @Transactional
    public int calculatePendingMonthFees(StudentBasicDTO student, Long classId, @NotNull Session sessionFrom) {
        List<Transaction> transactionList = transactionRepository.fetchStudentFeesTransactionByClassId(student.getId(), classId);
        LocalDateTime date = student.getCreatedDate()
            .isBefore(sessionFrom.getFromDate()
                .atStartOfDay())
            ? sessionFrom.getFromDate()
            .atStartOfDay()
            : student.getCreatedDate();
        if (!CollectionUtils.isEmpty(transactionList)) {
            for (Transaction transaction : transactionList) {
                Optional<LocalDate> tuition_fees = transaction
                    .getFeesLineItem()
                    .stream()
                    .filter(fees -> fees.getFeesProduct() != null && fees.getFeesProduct()
                        .getFeesName()
                        .equals("TUITION FEES"))
                    .map(u -> YearMonth.parse(u.getMonth(), DateTimeFormatter.ofPattern(MONTH_YEAR_FORMAT))
                        .atEndOfMonth())
                    .max(LocalDate::compareTo);
                if (tuition_fees.isPresent()) {
                    return Period
                        .between(tuition_fees.orElseThrow()
                            .withDayOfMonth(1), isDateAfterCurrent(sessionFrom.getToDate()))
                        .getMonths();
                }
            }
        }
        return Period.between(date.withDayOfMonth(1)
                .toLocalDate(), isDateAfterCurrent(sessionFrom.getToDate()))
            .getMonths();
    }

    @Transactional
    public void addTransport(TransportVm transport) {
        Student student = studentRepository.findById(transport.getStudentId())
            .orElseThrow();
        StudentToTransport studentToTransport = studentToTransportMapper.toEntity(transport);
        studentToTransport.setStudent(student);
        student.addTransport(studentToTransport);
        studentRepository.saveAndFlush(student);
    }

    public void discontinueTransport(Long id, Long locationId) {
        StudentToTransportId toTransportId = StudentToTransportId.builder()
            .studentId(id)
            .transportId(locationId)
            .build();
        StudentToTransport studentToTransport = studentToTransportRepository.findById(toTransportId)
            .orElseThrow();
        studentToTransport.setEndDate(Instant.now());
        studentToTransportRepository.saveAndFlush(studentToTransport);
    }
}
