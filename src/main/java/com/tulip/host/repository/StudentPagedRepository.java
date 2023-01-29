package com.tulip.host.repository;

import com.tulip.host.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface StudentPagedRepository extends JpaRepository<Student, Long>, QuerydslPredicateExecutor<Student> {}
