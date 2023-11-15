package com.tulip.host.web.rest.vm;

import com.tulip.host.data.ProductDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StockUpdateVM {

    ProductDTO product;
    int purchasedQty;

    @NotNull
    Long stockId;
}
