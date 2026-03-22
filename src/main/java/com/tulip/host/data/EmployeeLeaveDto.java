package com.tulip.host.data;

import com.tulip.host.web.rest.vm.FileUploadVM;
import java.util.List;
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
    // Populated only for approval-queue responses so the UI can call /action
    // without a separate notification lookup (same pattern as InventoryRequestDTO)
    private String machineId;
}
