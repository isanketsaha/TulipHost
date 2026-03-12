package com.tulip.host.web.rest;

import com.tulip.host.data.ClassSubjectDTO;
import com.tulip.host.data.CurriculumParametersDTO;
import com.tulip.host.service.CurriculumService;
import com.tulip.host.web.rest.vm.SaveCurriculumVM;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/curriculum")
@RequiredArgsConstructor
public class CurriculumController {

    private final CurriculumService curriculumService;

    @GetMapping("/parameters")
    public CurriculumParametersDTO getParameters() {
        return curriculumService.getParameters();
    }

    @GetMapping("/{classroomId}")
    public List<ClassSubjectDTO> getCurriculum(@PathVariable Long classroomId) {
        return curriculumService.getCurriculum(classroomId);
    }

    @PostMapping("/{classroomId}")
    public List<ClassSubjectDTO> saveCurriculum(@PathVariable Long classroomId, @Valid @RequestBody SaveCurriculumVM vm) {
        return curriculumService.saveCurriculum(classroomId, vm);
    }
}
