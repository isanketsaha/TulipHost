package com.tulip.host.web.rest;

import com.tulip.host.data.ClassDetailDTO;
import com.tulip.host.data.StudentBasicDTO;
import com.tulip.host.service.ClassroomService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/classroom")
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomService classroomService;

    @GetMapping("/all")
    List<ClassDetailDTO> fetch() {
        return classroomService.fetchAllClassroom();
    }

    @GetMapping("/studentList/{classroomId}")
    List<StudentBasicDTO> fetchStudentList(@PathVariable Long classroomId) {
        return classroomService.fetchStudentList(classroomId);
    }
}
