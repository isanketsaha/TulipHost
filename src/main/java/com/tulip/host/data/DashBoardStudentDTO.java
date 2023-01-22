package com.tulip.host.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashBoardStudentDTO {

    long schoolStrength;
    long studentAdmissionCountThisWeek;
    long studentAdmissionCountThisMonth;
}
