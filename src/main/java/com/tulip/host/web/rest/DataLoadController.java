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
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
            List<? extends DataLoadVM> data = map(vm.getType(), document);
            if (CollectionUtils.isNotEmpty(data)) {
                if (vm.getType().equals(UploadTypeEnum.PRODUCT)) {
                    productService.loadProducts((List<ProductLoadVM>) data);
                } else if (vm.getType().equals(UploadTypeEnum.FEES)) {
                    dataLoadService.loadFees((List<FeesLoadVM>) data);
                }
                dataLoadService.addToUpload(vm.getFile());
                return ResponseEntity.ok().build();
            }
        } catch (Exception e) {
            vm.getFile().setStatus("FAILED");
            dataLoadService.addToUpload(vm.getFile());
            throw new RuntimeException(e);
        }
        return ResponseEntity.internalServerError().build();
    }

    private List<? extends DataLoadVM> map(UploadTypeEnum uploadType, Document document) {
        return document.getSheet(uploadType.name(), uploadType.getFormat());
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

    @GetMapping(value = "/all")
    public List<FileUploadVM> get(@RequestParam(required = false) String type) {
        return dataLoadService.get(type);
    }

    @GetMapping(value = "/url")
    public String preSignedURL(@RequestParam String uuid) {
        return uploadService.getURL(uuid);
    }
}
