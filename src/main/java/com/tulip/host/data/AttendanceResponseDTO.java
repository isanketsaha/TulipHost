package com.tulip.host.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO to wrap the eOffice API response containing attendance/timesheet data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponseDTO {

    @JsonProperty("InOutPunchData")
    private List<EofficeAttendanceDTO> inOutPunchData;
}
