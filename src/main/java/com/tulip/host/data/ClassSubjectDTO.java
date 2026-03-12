package com.tulip.host.data;

import com.tulip.host.enums.ExamType;
import com.tulip.host.enums.TermMarkType;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassSubjectDTO implements Serializable {

    private Long id;
    private String subjectKey;
    private String displayName;
    private boolean included;
    private Set<ExamType> examTypes;
    private TermMarkType termMarkType;
    private List<ParameterDTO> assessmentParams;
    private List<ProductDTO> books;
}
