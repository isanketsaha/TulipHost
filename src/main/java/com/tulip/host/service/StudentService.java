package com.tulip.host.service;

import com.tulip.host.data.StudentBasicDTO;
import com.tulip.host.data.StudentDetailsDTO;
import com.tulip.host.domain.*;
import com.tulip.host.mapper.StudentMapper;
import com.tulip.host.repository.ClassDetailRepository;
import com.tulip.host.repository.DependentRepository;
import com.tulip.host.repository.StudentRepository;
import com.tulip.host.repository.UserToDependentRepository;
import com.tulip.host.web.rest.vm.OnboardingVM;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    private final ClassDetailRepository classDetailRepository;

    private final StudentMapper studentMapper;

    public List<StudentBasicDTO> fetchAllStudent() {
        List<Student> all = studentRepository.findAll();
        return studentMapper.getBasicEntityListFromModelList(all);
    }

    @Transactional
    public Long addStudent(OnboardingVM onboardingVM) {
        ClassDetail classDetail = classDetailRepository.findBySessionIdAndStd(onboardingVM.getSession(), onboardingVM.getStd().name());
        Student student = Student
            .builder()
            .gender(onboardingVM.getGender().getDisplayType())
            .bloodGroup(onboardingVM.getBloodGroup().getDisplayType())
            .phoneNumber(String.valueOf(onboardingVM.getContact()))
            .dob(onboardingVM.getDob())
            .religion(onboardingVM.getReligion().name())
            .previousSchool(onboardingVM.getPreviousSchool())
            .address(onboardingVM.getAddress())
            .name(onboardingVM.getName().toUpperCase())
            .build();
        student.addStd(StudentToClass.builder().std(classDetail).student(student).build());
        onboardingVM
            .getDependent()
            .forEach(dependent -> {
                Dependent item = Dependent
                    .builder()
                    .contact(String.valueOf(dependent.getContact()))
                    .name(dependent.getName().toUpperCase())
                    .occupation(dependent.getOccupation().toUpperCase())
                    .qualification(dependent.getQualification())
                    .relationship(dependent.getRelation().name())
                    .aadhaarNo(String.valueOf(dependent.getAadhaar()))
                    .build();
                student.addDependents(UserToDependent.builder().dependent(item).student(student).build());
            });
        Student save = studentRepository.save(student);
        return save.getId();
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
