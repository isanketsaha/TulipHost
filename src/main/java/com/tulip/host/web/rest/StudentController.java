package com.tulip.host.web.rest;

import com.tulip.host.data.StudentBasicDTO;
import com.tulip.host.data.StudentDetailsDTO;
import com.tulip.host.service.StudentService;
import com.tulip.host.web.rest.vm.UserEditVM;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @RequestMapping("/basicSearch")
    public StudentBasicDTO basicSearch(
        @Valid @RequestParam(value = "id", defaultValue = "0") long id,
        @Valid @RequestParam(value = "classId", defaultValue = "0") long classId
    ) {
        if (classId < 1) {
            return studentService.basicSearchStudent(id);
        } else {
            return studentService.basicSearchStudent(id, classId);
        }
    }

    @RequestMapping("/searchByName/{name}")
    public List<StudentBasicDTO> search(@Valid @PathVariable String name) {
        return studentService.searchStudent(name);
    }

    @PostMapping("/editStudentDetails")
    public void editDetails(@RequestBody UserEditVM editVM) {
        studentService.editStudentDetails(editVM);
    }

    @PreAuthorize("hasAuthority('UG_PRINCIPAL') or hasAuthority('UG_ADMIN')")
    @RequestMapping("/deactivate")
    public void deactivate(@Valid @RequestParam(value = "studentId") long id) {
        studentService.deactivate(id);
    }
}
