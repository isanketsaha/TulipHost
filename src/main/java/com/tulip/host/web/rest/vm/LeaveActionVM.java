package com.tulip.host.web.rest.vm;

import com.tulip.host.enums.LeaveEvents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveActionVM {
    private String machineId;
    private Long leaveId;
    private LeaveEvents action; // APPROVE or REJECT
    private String comments;
}
