package com.tulip.host.web.rest;

import com.tulip.host.data.GlobalDocumentDTO;
import com.tulip.host.enums.GlobalDocumentCategory;
import com.tulip.host.service.GlobalDocumentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.fileupload.FileUploadException;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/global-documents")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('UG_PRINCIPAL') or hasAuthority('UG_ADMIN')")
public class GlobalDocumentController {

    private final GlobalDocumentService globalDocumentService;

    @GetMapping
    public List<GlobalDocumentDTO> list() {
        return globalDocumentService.list();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public GlobalDocumentDTO upload(
        @RequestParam MultipartFile file,
        @RequestParam String name,
        @RequestParam(required = false) String description,
        @RequestParam GlobalDocumentCategory category
    ) throws FileUploadException {
        return globalDocumentService.upload(file, name, description, category);
    }

    @PatchMapping(value = "/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE })
    public GlobalDocumentDTO update(
        @PathVariable Long id,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String description,
        @RequestParam(required = false) GlobalDocumentCategory category,
        @RequestParam(required = false) MultipartFile file
    ) throws FileUploadException {
        return globalDocumentService.update(id, name, description, category, file);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        globalDocumentService.delete(id);
    }
}
