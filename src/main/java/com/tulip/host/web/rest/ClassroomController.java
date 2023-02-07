package com.tulip.host.web.rest;

import com.tulip.host.data.ClassDetailDTO;
import com.tulip.host.data.ClassListDTO;
import com.tulip.host.service.ClassroomService;
import com.tulip.host.web.rest.vm.PromoteStudentVM;
import java.util.List;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/classroom")
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomService classroomService;

    @GetMapping("/all")
    List<ClassListDTO> fetch() {
        return classroomService.fetchAllClasses();
    }

    @GetMapping("/details/{classroomId}")
    public ClassDetailDTO fetchStudentList(@PathVariable Long classroomId, HttpSession session) {
        return classroomService.fetchClassDetails(classroomId);
    }

    @PostMapping("/student-promote")
    public void promoteStudents(@Valid @RequestBody PromoteStudentVM promoteStudentVM) {
        classroomService.promoteStudents(promoteStudentVM);
    }
}
