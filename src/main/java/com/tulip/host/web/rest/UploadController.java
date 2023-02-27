package com.tulip.host.web.rest;

import com.tulip.host.service.UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileUploadException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
@Slf4j
public class UploadController {

    private final UploadService uploadService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String save(@ModelAttribute MultipartFile documents) throws FileUploadException {
        log.info(documents.toString());
        return uploadService.save(documents);
    }

    @GetMapping
    public String preSignedURL(@RequestParam String uuid) throws FileUploadException {
        return uploadService.preSignedURL(uuid);
    }

    @DeleteMapping
    public void delete(@RequestParam String uuid) throws FileUploadException {
        uploadService.delete(uuid);
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> download(@RequestParam String uuid) throws FileUploadException {
        byte[] download = uploadService.download(uuid);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "Sanket" + "\"").body(download);
    }
}
