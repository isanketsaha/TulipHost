package com.tulip.host.data;

import com.tulip.host.enums.InventoryRequestStatus;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class InventoryRequestDTO {

    private Long id;

    private Long employeeId;
    private String employeeName;

    private Long productId;
    private String productName;
    private String productCategory;

    private Integer qty;
    private String justification;

    private InventoryRequestStatus status;

    // Required by the UI to send the correct machineId when calling the action endpoint
    private String machineId;

    private String approvedBy;
    private LocalDateTime approvedDate;
    private String remarks;

    private String createdDate;
}
