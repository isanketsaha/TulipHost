package com.tulip.host.repository;

import com.tulip.host.data.StudentBasicDTO;
import com.tulip.host.data.StudentDetailsDTO;
import com.tulip.host.domain.Student;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<StudentBasicDTO> fetchAll(boolean isActive);

    public StudentDetailsDTO edit();

    public List<StudentBasicDTO> search(String name);

    public StudentDetailsDTO search(long id);
}
