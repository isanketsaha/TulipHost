package com.tulip.host.web.rest;

import com.tulip.host.enums.UploadTypeEnum;
import com.tulip.host.service.DataLoadService;
import com.tulip.host.service.ProductService;
import com.tulip.host.service.UploadService;
import com.tulip.host.web.rest.vm.FileUploadVM;
import com.tulip.host.web.rest.vm.UploadVM;
import com.tulip.host.web.rest.vm.dataload.DataLoadVM;
import com.tulip.host.web.rest.vm.dataload.FeesLoadVM;
import com.tulip.host.web.rest.vm.dataload.ProductLoadVM;
import com.tulip.host.web.rest.vm.dataload.SessionLoadVM;
import io.github.rushuat.ocell.document.Document;
import io.github.rushuat.ocell.document.DocumentOOXML;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
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

    private final UploadService uploadService;
    private final ProductService productService;

    @PostMapping(value = "/add", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> load(@RequestBody UploadVM vm) {
        try (Document document = new DocumentOOXML()) {
            byte[] download = Optional.of(uploadService.download(vm.getFile().getUid())).orElseThrow();
            document.fromBytes(download);

            List<? extends DataLoadVM> data = map(FeesLoadVM.class, document);

            if (CollectionUtils.isNotEmpty(data)) {
                // productService.loadProducts(products);
                //                    dataLoadService.addToUpload(vm.getFile());
                return ResponseEntity.ok("Loaded");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.internalServerError().body("Error");
    }

    private <T> List<? extends DataLoadVM> map(Class<? extends DataLoadVM> clazz, Document document) {
        return document.getSheet(clazz);
    }

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

    @PostMapping(value = "/addSession")
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

    @GetMapping(value = "/all")
    public List<FileUploadVM> get(@RequestParam(required = false) String type) {
        return dataLoadService.get(type);
    }

    @GetMapping(value = "/url")
    public String preSignedURL(@RequestParam String uuid) {
        return uploadService.getURL(uuid);
    }
}
