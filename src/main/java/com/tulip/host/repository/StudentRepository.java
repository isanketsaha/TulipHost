package com.tulip.host.repository;

import com.tulip.host.data.StudentDetailsDTO;
import com.tulip.host.domain.Student;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> fetchAll(boolean isActive);

    public StudentDetailsDTO edit();

    public List<Student> search(String name);

    public Student search(long id);
}
