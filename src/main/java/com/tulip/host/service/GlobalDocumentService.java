package com.tulip.host.service;

import com.tulip.host.data.GlobalDocumentDTO;
import com.tulip.host.domain.GlobalDocument;
import com.tulip.host.enums.GlobalDocumentCategory;
import com.tulip.host.repository.GlobalDocumentRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileUploadException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class GlobalDocumentService {

    private static final String S3_PREFIX = "documents/global";

    private final GlobalDocumentRepository repository;
    private final ObjectStorageService storageService;

    public List<GlobalDocumentDTO> list() {
        return repository.findAllByIsActiveTrueOrderByCreatedDateDesc().stream().map(this::toDto).toList();
    }

    @Transactional
    public GlobalDocumentDTO upload(MultipartFile file, String name, String description, GlobalDocumentCategory category)
        throws FileUploadException {
        String s3Key = storageService.save(file, storageService.getDocsBucket(), S3_PREFIX);
        GlobalDocument doc = new GlobalDocument();
        doc.setName(name);
        doc.setDescription(description);
        doc.setCategory(category);
        doc.setS3Key(s3Key);
        doc.setIsActive(true);
        return toDto(repository.save(doc));
    }

    @Transactional
    public GlobalDocumentDTO update(Long id, String name, String description, GlobalDocumentCategory category, MultipartFile file)
        throws FileUploadException {
        GlobalDocument doc = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Global document not found: " + id));
        if (name != null) doc.setName(name);
        if (description != null) doc.setDescription(description);
        if (category != null) doc.setCategory(category);
        if (file != null && !file.isEmpty()) {
            // Delete the old S3 object before uploading the replacement
            try {
                storageService.deleteObject(doc.getS3Key());
            } catch (Exception e) {
                log.warn("Could not delete old S3 object during update, key={}", doc.getS3Key(), e);
            }
            String newKey = storageService.save(file, storageService.getDocsBucket(), S3_PREFIX);
            doc.setS3Key(newKey);
        }
        return toDto(repository.save(doc));
    }

    @Transactional
    public void delete(Long id) {
        GlobalDocument doc = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Global document not found: " + id));
        try {
            storageService.deleteObject(doc.getS3Key());
        } catch (Exception e) {
            log.warn("Could not delete S3 object for global document id={}, key={}", id, doc.getS3Key(), e);
        }
        repository.delete(doc);
    }

    private GlobalDocumentDTO toDto(GlobalDocument doc) {
        String downloadUrl = null;
        try {
            downloadUrl = storageService.createURL(doc.getS3Key()).toString();
        } catch (Exception e) {
            log.warn("Could not generate download URL for global document id={}", doc.getId(), e);
        }
        return GlobalDocumentDTO.builder()
            .id(doc.getId())
            .name(doc.getName())
            .description(doc.getDescription())
            .category(doc.getCategory())
            .downloadUrl(downloadUrl)
            .build();
    }
}
