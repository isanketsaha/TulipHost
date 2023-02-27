package com.tulip.host.service;

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
        //       return preSignedURL(uuid).toString();
    }

    public String preSignedURL(String uuid) {
        return storageService.createURL(uuid).toString();
    }

    public void delete(String uuid) {
        storageService.deleteObject(uuid);
    }

    public byte[] download(String uuid) {
        return storageService.downloadObject(uuid);
    }
}
