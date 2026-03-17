package com.tulip.host.data;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentExamResultDTO {

    private String examType;
    private String examName;
    private List<StudentMarkEntryDTO> scores;
}
