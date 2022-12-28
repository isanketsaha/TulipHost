package com.tulip.host.service;

import com.tulip.host.data.StudentBasicDTO;
import com.tulip.host.data.StudentDetailsDTO;
import com.tulip.host.domain.Student;
import com.tulip.host.repository.StudentRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public List<StudentBasicDTO> fetchAllStudent() {
        return studentRepository.fetchAll();
    }

    public Student addStudent() {
        return studentRepository.add(null);
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
