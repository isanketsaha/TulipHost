package com.tulip.host.web.rest;

import com.tulip.host.data.StudentBasicDTO;
import com.tulip.host.data.StudentDetailsDTO;
import com.tulip.host.service.StudentService;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @RequestMapping("/all")
    public Page<StudentBasicDTO> fetchAll(
        @RequestParam(value = "page", defaultValue = "0") int pageNo,
        @RequestParam(name = "size", defaultValue = "15") int pageSize
    ) {
        return studentService.fetchAllStudent(pageNo, pageSize);
    }

    @RequestMapping("/search/{id}")
    public StudentDetailsDTO search(@Valid @PathVariable int id) {
        return studentService.searchStudent(id);
    }

    @RequestMapping("/basicSearch/{id}")
    public StudentBasicDTO basicSearch(@Valid @PathVariable int id) {
        return studentService.basicSearchStudent(id);
    }

    @RequestMapping("/searchByName/{name}")
    public List<StudentBasicDTO> search(@Valid @PathVariable String name) {
        return studentService.searchStudent(name);
    }

    @RolesAllowed("UG_ADMIN")
    @RequestMapping("/edit")
    public StudentDetailsDTO edit() {
        return studentService.editStudent();
    }
}
