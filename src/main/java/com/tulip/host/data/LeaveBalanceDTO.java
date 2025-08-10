package com.tulip.host.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveBalanceDTO {
    private Long id;
    private String name;
    private Double availableCount;
    private Integer totalAllowed;
    private Double usedCount;

    private String description;

    private Boolean isPaid;
    private Boolean isActive;
}