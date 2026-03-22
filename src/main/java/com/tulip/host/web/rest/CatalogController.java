package com.tulip.host.web.rest;

import com.tulip.host.data.FeesCatalogDTO;
import com.tulip.host.data.ProductDTO;
import com.tulip.host.data.TransportCatalogDto;
import com.tulip.host.service.CatalogService;
import com.tulip.host.service.ProductService;
import com.tulip.host.web.rest.vm.InventoryUpdateVM;
import jakarta.validation.Valid;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/catalog")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService catalogService;

    private final ProductService productService;

    @GetMapping("/fees/{classID}")
    public List<FeesCatalogDTO> feesCatalog(@PathVariable Long classID, @RequestParam Long studentId) {
        List<FeesCatalogDTO> feesCatalogDTOS = catalogService.fetchFeesCatalog(classID);
        feesCatalogDTOS.addAll(catalogService.fetchFeesCatalogByStudent(studentId));
        Collections.sort(feesCatalogDTOS, Comparator.comparing(FeesCatalogDTO::getName));
        return feesCatalogDTOS;
    }

    @GetMapping("/transport")
    public List<TransportCatalogDto> transportCatalog(@RequestParam Long sessionId) {
        List<TransportCatalogDto> transportCatalog = catalogService.fetchTransportCatalog(sessionId);
        Collections.sort(transportCatalog, Comparator.comparing(TransportCatalogDto::getAmount));
        return transportCatalog;
    }

    @GetMapping("/product/{classID}")
    public List<ProductDTO> productCatalog(@PathVariable Long classID, @RequestParam(required = false) String category) {
        return catalogService.productCatalogWithUnifiedPricing(classID, category);
    }

    @PostMapping("/updateStock")
    public void updateProduct(@Valid @RequestBody InventoryUpdateVM stockUpdateVM) {
        catalogService.updateProduct(stockUpdateVM);
    }

    @PostMapping("/bulkRefillStock")
    public void bulkRefillStock(@Valid @RequestBody List<InventoryUpdateVM> items) {
        items.forEach(catalogService::updateProduct);
    }

    @PreAuthorize("hasAuthority('UG_ADMIN')")
    @PostMapping("/productVisibility")
    public void productVisibility(@RequestParam long productId) {
        productService.updateProductVisibility(productId, false);
    }
}
