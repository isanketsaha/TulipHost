package com.tulip.host.web.rest;

import com.tulip.host.service.UploadService;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.io.IOUtils;
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
    public String preSignedURL(@RequestParam String uuid) {
        return uploadService.preSignedURL(uuid);
    }

    @DeleteMapping
    public void delete(@RequestParam String uuid) {
        uploadService.delete(uuid);
    }

    @GetMapping("/download")
    public void download(@RequestParam String uuid, HttpServletResponse response) throws FileUploadException, IOException {
        byte[] download = uploadService.download(uuid);
        ByteArrayInputStream output = new ByteArrayInputStream(download);
        response.setContentType("text/png; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + "Sanket");
        response.setHeader("filename", "Sanket");
        IOUtils.copy(output, response.getOutputStream());
        IOUtils.closeQuietly(output);
    }
}
