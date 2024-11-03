package com.tulip.host.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.tulip.host.web.rest.vm.FileUploadVM;
import lombok.RequiredArgsConstructor;
import org.apache.commons.fileupload.FileUploadException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UploadService {

    private final ObjectStorageService storageService;

    public String save(MultipartFile documents) throws FileUploadException {
        return storageService.save(documents);
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
