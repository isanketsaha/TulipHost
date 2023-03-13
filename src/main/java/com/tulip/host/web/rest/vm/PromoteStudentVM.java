package com.tulip.host.web.rest.vm;

import com.tulip.host.enums.StdEnum;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromoteStudentVM {

    List<Long> studentId;
    Long sessionId;
    StdEnum std;
}
