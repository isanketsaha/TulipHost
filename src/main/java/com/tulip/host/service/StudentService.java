package com.tulip.host.service;

import com.tulip.host.data.ClassDetailDTO;
import com.tulip.host.data.StudentBasicDTO;
import com.tulip.host.data.StudentDetailsDTO;
import com.tulip.host.domain.Dependent;
import com.tulip.host.domain.Student;
import com.tulip.host.repository.ClassDetailRepository;
import com.tulip.host.repository.DependentRepository;
import com.tulip.host.repository.StudentRepository;
import com.tulip.host.web.rest.vm.DependentVM;
import com.tulip.host.web.rest.vm.OnboardingVM;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    private final ClassDetailRepository classDetailRepository;

    private final DependentRepository dependentRepository;

    public List<StudentBasicDTO> fetchAllStudent() {
        return studentRepository.fetchAll();
    }

    @Transactional
    public Long addStudent(OnboardingVM onboardingVM) {
        ClassDetailDTO classDetailDTO = classDetailRepository.fetchClass(onboardingVM.getSession(), onboardingVM.getStd());

        Student student = Student
            .builder()
            .gender(onboardingVM.getGender().getDisplayType())
            .bloodGroup(onboardingVM.getBloodGroup().getDisplayType())
            .std(classDetailDTO.getId())
            .phoneNumber(String.valueOf(onboardingVM.getContact()))
            .dob(onboardingVM.getDob())
            .isActive(Boolean.TRUE)
            .religion(onboardingVM.getReligion().name())
            .previousSchool(onboardingVM.getPreviousSchool())
            .address(onboardingVM.getAddress())
            .name(onboardingVM.getName().toUpperCase())
            .build();

        Student admission = studentRepository.save(student);
        for (DependentVM dependent : onboardingVM.getDependent()) {
            Dependent build = Dependent
                .builder()
                .contact(String.valueOf(dependent.getContact()))
                .name(dependent.getName().toUpperCase())
                .occupation(dependent.getOccupation().toUpperCase())
                .qualification(dependent.getQualification().toUpperCase())
                .relationship(dependent.getRelation().name())
                .aadhaarNo(String.valueOf(dependent.getAadhaar()))
                .student(student.getId())
                .build();
            dependentRepository.save(build);
        }
        return admission.getId();
    }

    public List<StudentBasicDTO> searchStudent(String name) {
        return studentRepository.search(name);
    }

    public StudentDetailsDTO searchStudent(long id) {
        return studentRepository.search(id);
    }

    public StudentDetailsDTO editStudent() {
        return studentRepository.edit();
    }
}
