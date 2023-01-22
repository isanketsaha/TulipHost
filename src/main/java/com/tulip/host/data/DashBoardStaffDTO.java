package com.tulip.host.data;

import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashBoardStaffDTO {

    long staffCount;
    Map<String, Long> staffTypeCount;
}
