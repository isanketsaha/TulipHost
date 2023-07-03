package com.tulip.host.web.rest;

import com.tulip.host.data.FeesCatalogDTO;
import com.tulip.host.data.ProductDTO;
import com.tulip.host.service.CatalogService;
import com.tulip.host.web.rest.vm.StockUpdateVM;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/catalog")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService catalogService;

    @GetMapping("/fees/{classID}")
    public List<FeesCatalogDTO> feesCatalog(@PathVariable Long classID) {
        List<FeesCatalogDTO> feesCatalogDTOS = catalogService.fetchFeesCatalog(classID);
        Collections.sort(feesCatalogDTOS, Comparator.comparing(FeesCatalogDTO::getName));
        return feesCatalogDTOS;
    }

    @GetMapping("/product/{classID}")
    public List<ProductDTO> productCatalog(@PathVariable Long classID) {
        return catalogService.productCatalog(classID);
    }

    @PostMapping("/productStock")
    public void updateProduct(@Valid @RequestBody StockUpdateVM stockUpdateVM) {
        catalogService.updateProduct(stockUpdateVM);
    }
}
