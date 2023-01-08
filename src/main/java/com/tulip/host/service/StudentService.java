package com.tulip.host.service;

import com.tulip.host.data.ClassDetailDTO;
import com.tulip.host.data.StudentBasicDTO;
import com.tulip.host.data.StudentDetailsDTO;
import com.tulip.host.domain.ClassDetail;
import com.tulip.host.domain.Dependent;
import com.tulip.host.domain.Student;
import com.tulip.host.mapper.ClassMapper;
import com.tulip.host.mapper.StudentMapper;
import com.tulip.host.repository.ClassDetailRepository;
import com.tulip.host.repository.DependentRepository;
import com.tulip.host.repository.StudentRepository;
import com.tulip.host.web.rest.vm.DependentVM;
import com.tulip.host.web.rest.vm.OnboardingVM;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    private final ClassDetailRepository classDetailRepository;

    private final DependentRepository dependentRepository;

    private final StudentMapper studentMapper;

    public List<StudentBasicDTO> fetchAllStudent() {
        return studentRepository.fetchAll();
    }

    @Transactional
    public Long addStudent(OnboardingVM onboardingVM) {
        ClassDetail classDetail = classDetailRepository.findBySessionIdAndStd(onboardingVM.getSession(), onboardingVM.getStd().name());
        Student student = Student
            .builder()
            .gender(onboardingVM.getGender().getDisplayType())
            .bloodGroup(onboardingVM.getBloodGroup().getDisplayType())
            .std(classDetail)
            .phoneNumber(String.valueOf(onboardingVM.getContact()))
            .dob(onboardingVM.getDob())
            .religion(onboardingVM.getReligion().name())
            .previousSchool(onboardingVM.getPreviousSchool())
            .address(onboardingVM.getAddress())
            .name(onboardingVM.getName().toUpperCase())
            .build();
        List<Dependent> dependentList = onboardingVM
            .getDependent()
            .stream()
            .map(dependent -> {
                return Dependent
                    .builder()
                    .contact(String.valueOf(dependent.getContact()))
                    .name(dependent.getName().toUpperCase())
                    .occupation(dependent.getOccupation().toUpperCase())
                    .qualification(dependent.getQualification())
                    .relationship(dependent.getRelation().name())
                    .aadhaarNo(String.valueOf(dependent.getAadhaar()))
                    .student(student)
                    .build();
            })
            .collect(Collectors.toList());
        List<Dependent> dependents = dependentRepository.saveAllAndFlush(dependentList);
        return dependents.stream().findFirst().get().getStudent().getId();
    }

    public List<StudentBasicDTO> searchStudent(String name) {
        return studentRepository.search(name);
    }

    public StudentDetailsDTO searchStudent(long id) {
        Student byId = studentRepository.findById(id).orElse(null);
        if (byId != null) {
            return studentMapper.getEntityFromModel(byId);
        }
        return null;
    }

    public StudentBasicDTO basicSearchStudent(long id) {
        Student byId = studentRepository.findById(id).orElse(null);
        if (byId != null) {
            return studentMapper.getBasicEntityFromModel(byId);
        }
        return null;
    }

    public StudentDetailsDTO editStudent() {
        return studentRepository.edit();
    }
}
