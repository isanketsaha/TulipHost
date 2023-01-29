package com.tulip.host.service;

import com.tulip.host.data.ClassDetailDTO;
import com.tulip.host.data.StudentBasicDTO;
import com.tulip.host.data.StudentDetailsDTO;
import com.tulip.host.domain.ClassDetail;
import com.tulip.host.domain.Student;
import com.tulip.host.mapper.ClassMapper;
import com.tulip.host.mapper.StudentMapper;
import com.tulip.host.repository.ClassDetailRepository;
import com.tulip.host.repository.StudentPagedRepository;
import com.tulip.host.repository.StudentRepository;
import com.tulip.host.utils.CommonUtils;
import com.tulip.host.web.rest.vm.OnboardingVM;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    private final ClassDetailRepository classDetailRepository;

    private final StudentMapper studentMapper;

    private final ClassMapper classMapper;

    private final StudentPagedRepository studentPagedRepository;

    @Transactional
    public Page<StudentBasicDTO> fetchAllStudent(int pageNo, int pageSize) {
        Page<Student> students = studentPagedRepository.findAll(
            CommonUtils.getPageRequest(Sort.Direction.DESC, "createdDate", pageNo, pageSize)
        );
        List<StudentBasicDTO> studentBasicDTOS = studentMapper.toBasicEntityList(students.getContent());
        return new PageImpl<>(studentBasicDTOS, students.getPageable(), students.getTotalElements());
    }

    @Transactional
    public Long addStudent(OnboardingVM onboardingVM) {
        ClassDetail classDetail = classDetailRepository.findBySessionIdAndStd(onboardingVM.getSession(), onboardingVM.getStd().name());
        Student student = studentMapper.toModel(onboardingVM);
        student.addClass(classDetail);
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
            ClassDetail classDetail = byId.getClassDetails().stream().findFirst().orElse(null);
            ClassDetailDTO classDetailDTO = classMapper.toEntity(classDetail);
            StudentDetailsDTO studentDetailsDTO = studentMapper.toDetailEntity(byId);
            studentDetailsDTO.setClassDetails(classDetailDTO);
            return studentDetailsDTO;
        }
        return null;
    }

    @Transactional
    public StudentBasicDTO basicSearchStudent(long id) {
        Student byId = studentRepository.findById(id).orElse(null);
        if (byId != null) {
            return studentMapper.toBasicEntity(byId);
        }
        return null;
    }

    public StudentDetailsDTO editStudent() {
        return studentRepository.edit();
    }
}
