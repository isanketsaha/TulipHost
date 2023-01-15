package com.tulip.host.domain;

import javax.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@ToString
@Entity
@Table(name = "student_to_class")
public class StudentToClass {

    @EmbeddedId
    private StudentToClassId id = new StudentToClassId();

    public StudentToClass() {}

    public StudentToClass(StudentToClassId id, ClassDetail std, Student student) {
        this.id = new StudentToClassId(std.getId(), student.getId());
        this.std = std;
        this.student = student;
    }

    @MapsId("classId")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "class_id", nullable = false)
    private ClassDetail std;

    @MapsId("studentId")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
}
