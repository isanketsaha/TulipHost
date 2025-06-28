package com.tulip.host.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.tulip.host.web.rest.vm.FileUploadVM;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileUploadException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class UploadService {

    private final ObjectStorageService storageService;

    public String save(MultipartFile documents) throws FileUploadException {
        try {
            log.info("Starting file upload for: {}", documents.getOriginalFilename());
            String result = storageService.save(documents);
            log.info("File upload completed successfully: {}", result);
            return result;
        } catch (Exception e) {
            log.error("File upload failed for: {}", documents.getOriginalFilename(), e);
            throw new FileUploadException("Failed to upload file: " + e.getMessage());
        }
    }

    @Async
    public CompletableFuture<FileUploadVM> saveAsync(byte[] documents, String mediaType, String docsType) {
        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(mediaType);
            String uid = storageService.save(documents, objectMetadata);
            FileUploadVM result = FileUploadVM.builder()
                    .status("done")
                    .type(mediaType)
                    .uid(uid)
                    .documentType(docsType)
                    .build();
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            log.error("Async file upload failed", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    public FileUploadVM save(byte[] documents, String mediaType, String docsType) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(mediaType);
        String uid = storageService.save(documents, objectMetadata);
        return FileUploadVM.builder().status("done").type(mediaType).uid(uid).documentType(docsType).build();
    }

    public String getURL(String uuid) {
        return storageService.createURL(uuid).toString();
    }

    public void delete(String uuid) {
        storageService.deleteObject(uuid);
    }

    public byte[] download(String uuid) {
        return storageService.downloadObject(uuid);
    }
}
