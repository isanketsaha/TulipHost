package com.tulip.host.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO for individual Attendance/Timesheet record from eOffice external service.
 * Maps to the InOutPunchData structure returned by eOffice API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = { "empcode", "dateString" })
public class EofficeAttendanceDTO implements Serializable {

    @JsonProperty("Empcode")
    private String empcode;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("INTime")
    private String inTime;

    @JsonProperty("OUTTime")
    private String outTime;

    @JsonProperty("WorkTime")
    private String workTime;

    @JsonProperty("OverTime")
    private String overTime;

    @JsonProperty("BreakTime")
    private String breakTime;

    @JsonProperty("Status")
    private String status;

    @JsonProperty("DateString")
    private String dateString;

    @JsonProperty("Remark")
    private String remark;

    @JsonProperty("Erl_Out")
    private String erlOut;

    @JsonProperty("Late_In")
    private String lateIn;
}
