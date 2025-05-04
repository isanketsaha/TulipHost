package com.tulip.host.web.rest;

import com.tulip.host.enums.UploadTypeEnum;
import com.tulip.host.service.CalendarService;
import com.tulip.host.service.DataLoadService;
import com.tulip.host.service.ProductService;
import com.tulip.host.service.UploadService;
import com.tulip.host.web.rest.vm.FileUploadVM;
import com.tulip.host.web.rest.vm.UploadVM;
import com.tulip.host.web.rest.vm.dataload.CalenderLoadVM;
import com.tulip.host.web.rest.vm.dataload.DataLoadVM;
import com.tulip.host.web.rest.vm.dataload.FeesLoadVM;
import com.tulip.host.web.rest.vm.dataload.ProductLoadVM;
import com.tulip.host.web.rest.vm.dataload.SessionLoadVM;
import io.github.rushuat.ocell.document.Document;
import io.github.rushuat.ocell.document.DocumentOOXML;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final CalendarService calendarService;

    @PostMapping(value = "/add", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> load(@RequestBody UploadVM vm) {
        try (Document document = new DocumentOOXML()) {
            byte[] download = Optional.of(uploadService.download(vm.getFile().getUid())).orElseThrow();
            document.fromBytes(download);
            List<? extends Object> data = map(vm.getType(), document);
            if (CollectionUtils.isNotEmpty(data)) {
                if (vm.getType().equals(UploadTypeEnum.PRODUCT)) {
                    productService.loadProducts((List<ProductLoadVM>) data);
                } else if (vm.getType().equals(UploadTypeEnum.FEES)) {
                    dataLoadService.loadFees((List<FeesLoadVM>) data);
                }
                else if (vm.getType().equals(UploadTypeEnum.CALENDER)) {
                    calendarService.createEvent((List<CalenderLoadVM>) data);
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

    private List<? extends Object> map(UploadTypeEnum uploadType, Document document) {
        List<? extends Object> rows = document.getSheet(uploadType.name(), uploadType.getFormat());
        if (rows == null || rows.isEmpty()) {
            log.info("No rows found in sheet for upload type: {}", uploadType);
            return Collections.emptyList();
        }

        // Filter out blank rows
        List<Object> filteredRows = rows.stream()
            .filter(row -> isRowBlank(row))
            .collect(Collectors.toList());

        log.debug("Filtered {} blank rows, {} valid rows remain.", rows.size() - filteredRows.size(), filteredRows.size());
        return filteredRows;
    }


    private boolean isRowBlank(Object row) {
        if (row == null) {
            return true;
        }

        try {
            // Use reflection to check all fields
            for (Field field : row.getClass().getDeclaredFields()) {
                field.setAccessible(true); // Allow access to private fields
                Object value = field.get(row);
                if (value != null) {
                    return true; // Found a non-empty value, row is not blank
                }
            }
            return false; // All fields are null or empty
        } catch (IllegalAccessException e) {
            log.warn("Failed to inspect row object: {}. Treating as non-blank.", row.getClass().getName(), e);
            return false;
        }
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
