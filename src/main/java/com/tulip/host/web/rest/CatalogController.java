package com.tulip.host.web.rest;

import com.tulip.host.data.FeesCatalogDTO;
import com.tulip.host.domain.FeesCatalog;
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

    @PostMapping("/fees")
    public List<FeesCatalogDTO> feesCatalog(@RequestBody CatalogVM catalogVM) {
        return catalogService.fetchFeesCatalog(catalogVM);
    }
}
