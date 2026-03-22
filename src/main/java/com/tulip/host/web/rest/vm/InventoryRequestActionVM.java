package com.tulip.host.web.rest.vm;

import com.tulip.host.enums.InventoryRequestEvents;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InventoryRequestActionVM {

    @NotBlank
    private String machineId;

    @NotNull
    private Long requestId;

    // APPROVE, REJECT, or FULFILL
    @NotNull
    private InventoryRequestEvents action;

    private String remarks;
}
