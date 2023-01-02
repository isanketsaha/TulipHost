package com.tulip.host.web.rest;

import com.tulip.host.data.StudentBasicDTO;
import com.tulip.host.data.StudentDetailsDTO;
import com.tulip.host.service.StudentService;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @RequestMapping("/all")
    public List<StudentBasicDTO> fetchAll() {
        return studentService.fetchAllStudent();
    }

    @RolesAllowed({ "UG_ADMIN", "UG_PRINCIPAL" })
    @RequestMapping("/add")
    public void add() {
        //        studentService.addStudent();
    }

    @RequestMapping("/search/{id}")
    public StudentDetailsDTO search(@Valid @PathVariable int id) {
        return studentService.searchStudent(id);
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
