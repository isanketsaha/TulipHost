package com.tulip.host.data;

import lombok.Data;

@Data
public class EmployeeLeaveDto {
    private Long id;
    private String employeeId;
    private String employeeTid;
    private String employeeName;
    private LeaveTypeDto leaveType;
    private String startDate;
    private String endDate;
    private Double totalDays;
    private String reason;
    private String status;
    private String approvedBy;
    private String approvalDate;
    private String comments;
    private String createdBy;
    private String lastModifiedBy;
    private String createdDate;
    private String lastModifiedDate;

}