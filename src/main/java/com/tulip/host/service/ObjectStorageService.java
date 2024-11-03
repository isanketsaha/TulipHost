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
        LocalDate date = LocalDate.now().plusDays(6);
        URL url = amazonS3Client.generatePresignedUrl(
            properties.getAws().getCredential().getBucketName(),
            key,
            Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
        );

        log.info("Generated the signature " + url);
        return url;
    }

    public byte[] downloadObject(String keys) {
        S3Object s3object = amazonS3Client.getObject(properties.getAws().getCredential().getBucketName(), keys);
        S3ObjectInputStream objectContent = s3object.getObjectContent();
        try {
            return IOUtils.toByteArray(objectContent);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new IllegalStateException("Failed to download the file", e);
        }
    }

    public void deleteObject(String key) {
        amazonS3Client.deleteObject(properties.getAws().getCredential().getBucketName(), key);
    }

    public String save(MultipartFile file) throws FileUploadException {
        final String FOLDER = CommonUtils.formatFromDate(LocalDate.now(), "MMM-yyyy") + "/";
        try {
            String uuid = UUID.randomUUID().toString().replace("-", "");
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(file.getContentType());
            objectMetadata.setContentLength(file.getSize());
            amazonS3Client.putObject(
                properties.getAws().getCredential().getBucketName(),
                FOLDER + uuid,
                file.getInputStream(),
                objectMetadata
            );
            return FOLDER + uuid;
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileUploadException("Unable to Upload");
        }
    }

    public String save(byte[] file, ObjectMetadata objectMetadata){
        final String FOLDER = CommonUtils.formatFromDate(LocalDate.now(), "MMM-yyyy") + "/";
        String uuid = UUID.randomUUID().toString().replace("-", "");
        objectMetadata.setContentLength(file.length);
        amazonS3Client.putObject(
            properties.getAws().getCredential().getBucketName(),
            FOLDER + uuid,
            new ByteArrayInputStream(file),
            objectMetadata
        );
        return FOLDER + uuid;
    }
}
