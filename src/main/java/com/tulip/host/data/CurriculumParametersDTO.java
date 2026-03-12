package com.tulip.host.data;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurriculumParametersDTO implements Serializable {

    private List<ParameterDTO> assessmentParams;
}
