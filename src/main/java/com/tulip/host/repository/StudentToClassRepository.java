package com.tulip.host.repository;

import com.tulip.host.domain.StudentToClass;
import com.tulip.host.domain.StudentToClassId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentToClassRepository extends JpaRepository<StudentToClass, StudentToClassId> {}
