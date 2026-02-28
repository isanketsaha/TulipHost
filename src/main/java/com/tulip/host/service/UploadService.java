package com.tulip.host.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.tulip.host.utils.CommonUtils;
import com.tulip.host.web.rest.vm.FileUploadVM;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileUploadException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class UploadService {

    private final ObjectStorageService storageService;

    public String save(MultipartFile documents, String prefix) throws FileUploadException {
        try {
            log.info("Starting file upload for: {}", documents.getOriginalFilename());
            String result = storageService.save(documents, storageService.getDocsBucket(), prefix);
            String sanitizedResult = CommonUtils.sanitizeKey(result);
            log.info("File upload completed successfully: {}", sanitizedResult);
            return sanitizedResult;
        } catch (Exception e) {
            log.error("File upload failed for: {}", documents.getOriginalFilename(), e);
            throw new FileUploadException("Failed to upload file: " + e.getMessage());
        }
    }

    @Async
    public CompletableFuture<FileUploadVM> saveAsync(
        byte[] documents,
        String mediaType,
        String docsType,
        String bucketName,
        String prefix
    ) {
        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(mediaType);
            String uid = storageService.save(documents, objectMetadata, bucketName, prefix);
            FileUploadVM result = FileUploadVM.builder().status("done").type(mediaType).uid(uid).documentType(docsType).build();
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            log.error("Async file upload failed", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    public FileUploadVM save(byte[] documents, String mediaType, String docsType, String bucketName, String prefix) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(mediaType);
        String uid = storageService.save(documents, objectMetadata, bucketName, prefix);
        return FileUploadVM.builder().status("done").type(mediaType).uid(uid).documentType(docsType).build();
    }

    public String getURL(String uuid) {
        return storageService.createURL(uuid).toString();
    }

    public String getURL(String uuid, String bucketName) {
        return storageService.createURL(uuid, bucketName).toString();
    }

    public void delete(String uuid) {
        storageService.deleteObject(uuid);
    }

    public void delete(String uuid, String bucketName) {
        storageService.deleteObject(uuid, bucketName);
    }

    public byte[] download(String uuid) {
        return storageService.downloadObject(uuid);
    }

    public byte[] download(String uuid, String bucketName) {
        return storageService.downloadObject(uuid, bucketName);
    }

    public String getDocsBucket() {
        return storageService.getDocsBucket();
    }

    public String getInvoiceBucket() {
        return storageService.getInvoiceBucket();
    }
}
