package com.tulip.host.web.rest.vm;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmitInventoryRequestVM {

    @NotNull
    private Long productCatalogId;

    @NotNull
    @Min(1)
    private Integer qty;

    private String justification;
}
