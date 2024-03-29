package com.tulip.host.web.rest;

import com.tulip.host.data.ClassDetailDTO;
import com.tulip.host.data.ClassListDTO;
import com.tulip.host.data.FeesCatalogDTO;
import com.tulip.host.enums.FeesRuleType;
import com.tulip.host.service.ClassroomService;
import com.tulip.host.web.rest.vm.FeesFilterVM;
import com.tulip.host.web.rest.vm.PromoteStudentVM;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.xml.bind.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/classroom")
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomService classroomService;

    @GetMapping("/all")
    List<ClassListDTO> fetch(@RequestParam Optional<Integer> sessionId) {
        return classroomService.fetchAllClasses(sessionId.orElseGet(() -> 0));
    }

    @GetMapping("/details/{classroomId}")
    public ClassDetailDTO fetchStudentList(@PathVariable Long classroomId) {
        return classroomService.fetchClassDetails(classroomId);
    }

    @PostMapping("/feesByRule")
    public Map<String, List<FeesCatalogDTO>> getFees(@Valid @RequestBody FeesFilterVM rule) {
        return classroomService.getFees(rule);
    }

    @PostMapping("/student-promote")
    public void promoteStudents(@Valid @RequestBody PromoteStudentVM promoteStudentVM)
        throws ValidationException, jakarta.xml.bind.ValidationException {
        classroomService.promoteStudents(promoteStudentVM);
    }

    @GetMapping("/id")
    public Long fetchClassId(@RequestParam(value = "std") String std, @RequestParam(name = "sessionId") Long sessionId) {
        return classroomService.fetchClassDetails(std, sessionId);
    }
}
