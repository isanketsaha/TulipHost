package com.tulip.host.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentMarkEntryDTO {

    private String subjectKey;
    private String scoreType;
    private short marks;
    private int maxMarks;
}
