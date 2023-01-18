package com.tulip.host.web.rest;

import com.tulip.host.data.CatalogDTO;
import com.tulip.host.data.FeesCatalogDTO;
import com.tulip.host.service.CatalogService;
import com.tulip.host.web.rest.vm.CatalogVM;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/catalog")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService catalogService;

    @GetMapping("/fees/{classID}")
    public List<FeesCatalogDTO> feesCatalog(@PathVariable Long classID) {
        return catalogService.fetchFeesCatalog(classID);
    }

    @GetMapping("/product/{classID}")
    public List<CatalogDTO> productCatalog(@PathVariable Long classID) {
        return catalogService.productCatalog(classID);
    }
}
