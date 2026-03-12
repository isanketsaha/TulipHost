package com.tulip.host.web.rest.vm;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;

@Data
public class SaveCurriculumVM {

    @NotEmpty
    @Valid
    private List<ClassSubjectVM> subjects;
}
