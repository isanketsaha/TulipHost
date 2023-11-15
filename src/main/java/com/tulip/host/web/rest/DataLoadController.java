package com.tulip.host.web.rest;

import com.tulip.host.service.DataLoadService;
import com.tulip.host.service.FileUploaderService;
import com.tulip.host.service.ProductService;
import com.tulip.host.web.rest.vm.FeesLoadVM;
import com.tulip.host.web.rest.vm.ProductLoadVM;
import com.tulip.host.web.rest.vm.SessionLoadVM;
import io.github.rushuat.ocell.document.Document;
import io.github.rushuat.ocell.document.DocumentOOXML;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/dataload")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('UG_ADMIN')")
public class DataLoadController {

    private final DataLoadService dataLoadService;

    private final FileUploaderService fileUploaderService;

    private final ProductService productService;

    @PostMapping(value = "/newFees", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> uploadNewFees(@RequestBody MultipartFile file) {
        try (Document document = new DocumentOOXML()) {
            document.fromStream(file.getInputStream());

            List<FeesLoadVM> fees = document.getSheet("fees", FeesLoadVM.class);
            if (CollectionUtils.isNotEmpty(fees)) {
                dataLoadService.loadFees(fees);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok("Success");
    }

    @PostMapping(value = "/newSession")
    public ResponseEntity addSession(@RequestBody SessionLoadVM loadVM) {
        dataLoadService.loadSession(loadVM);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/session")
    public void removeSession(@RequestParam Long id) {
        dataLoadService.removeSession(id);
    }

    @PostMapping(
        value = "/newProducts",
        consumes = { MediaType.MULTIPART_FORM_DATA_VALUE },
        produces = { MediaType.APPLICATION_JSON_VALUE }
    )
    public ResponseEntity<String> uploadNewProducts(@RequestBody MultipartFile file) {
        try (Document document = new DocumentOOXML()) {
            document.fromStream(file.getInputStream());

            List<ProductLoadVM> products = document.getSheet("product", ProductLoadVM.class);
            if (CollectionUtils.isNotEmpty(products)) {
                productService.loadProducts(products);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok("Success");
    }
}
