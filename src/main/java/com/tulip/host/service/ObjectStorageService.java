package com.tulip.host.service;

import com.amazonaws.services.ecr.model.UploadNotFoundException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.tulip.host.config.ApplicationProperties;
import com.tulip.host.utils.CommonUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class ObjectStorageService {

    private final AmazonS3 amazonS3Client;

    private final ApplicationProperties properties;

    public List<S3ObjectSummary> listObjectsInBucket(String bucket) {
        List<S3ObjectSummary> collect = amazonS3Client.listObjectsV2(bucket).getObjectSummaries();
        log.info("Found " + collect.size() + " objects in the bucket " + bucket);
        return collect;
    }

    public URL createURL(String key) {
        return createURL(key, getDocsBucket());
    }

    public URL createURL(String key, String bucketName) {
        LocalDate date = LocalDate.now().plusDays(6);
        return amazonS3Client.generatePresignedUrl(
            bucketName,
            key,
            Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
        );
    }

    public String createPublicURL(String key, String bucketName) {
        String region = properties.getAws().getRegion().getValue();
        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + key;
    }

    public byte[] downloadObject(String keys) {
        return downloadObject(keys, getDocsBucket());
    }

    public byte[] downloadObject(String keys, String bucketName) {
        S3Object s3object = amazonS3Client.getObject(bucketName, keys);
        S3ObjectInputStream objectContent = s3object.getObjectContent();
        try {
            return IOUtils.toByteArray(objectContent);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new IllegalStateException("Failed to download the file", e);
        }
    }

    public void deleteObject(String key) {
        deleteObject(key, getDocsBucket());
    }

    public void deleteObject(String key, String bucketName) {
        amazonS3Client.deleteObject(bucketName, key);
    }

    public String save(MultipartFile file) throws FileUploadException {
        return save(file, getDocsBucket(), null);
    }

    public String save(MultipartFile file, String bucketName, String prefix) throws FileUploadException {
        // Always include date folder (MMM/dd), with optional prefix prepended
        String dateFolder = CommonUtils.formatFromDate(LocalDate.now(), "MMM-yyyy");
        final String FOLDER = (prefix != null ? CommonUtils.sanitizeFileName(prefix) + "/" + dateFolder + "/" : dateFolder + "/");
        try {
            String fileName = file.getOriginalFilename();
            if (fileName == null || fileName.isBlank()) {
                fileName = UUID.randomUUID().toString().replace("-", "");
            }
            // Sanitize filename before storing to prevent injection and ensure key consistency
            fileName = CommonUtils.sanitizeFileName(fileName);
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(file.getContentType());
            objectMetadata.setContentLength(file.getSize());
            amazonS3Client.putObject(bucketName, FOLDER + fileName, file.getInputStream(), objectMetadata);
            return FOLDER + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileUploadException("Unable to Upload");
        }
    }

    public String save(byte[] file, ObjectMetadata objectMetadata) {
        return save(file, objectMetadata, getDocsBucket(), null);
    }

    public String save(byte[] file, ObjectMetadata objectMetadata, String bucketName, String prefix) {
        // If prefix provided, use directly; otherwise use month folder
        final String FOLDER = (prefix != null ? prefix + "/" : CommonUtils.formatFromDate(LocalDate.now(), "MMM-yyyy") + "/");
        String uuid = UUID.randomUUID().toString().replace("-", "");
        objectMetadata.setContentLength(file.length);
        amazonS3Client.putObject(bucketName, FOLDER + uuid, new ByteArrayInputStream(file), objectMetadata);
        return FOLDER + uuid;
    }

    public String getDocsBucket() {
        return properties.getAws().getCredential().getBucketName();
    }

    public String getInvoiceBucket() {
        return properties.getAws().getCredential().getInvoiceBucketName();
    }
}
