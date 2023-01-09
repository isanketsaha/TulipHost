package com.tulip.host.web.rest;

import com.tulip.host.service.FileUploaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final FileUploaderService fileUploaderService;

    @PostMapping(value = "/uploadFees", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> uploadFeesStructure(@RequestBody MultipartFile file) {
        fileUploaderService.uploadFile(file);
        return ResponseEntity.ok("Success");
    }
}
