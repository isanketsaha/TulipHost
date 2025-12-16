package com.tulip.host.data;

import java.util.List;

import com.tulip.host.web.rest.vm.FileUploadVM;

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
    private Boolean isHalfDay;
    private List<FileUploadVM> documents;
    private String createdBy;
    private String lastModifiedBy;
    private String createdDate;
    private String lastModifiedDate;

}
