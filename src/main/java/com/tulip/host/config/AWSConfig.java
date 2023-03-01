package com.tulip.host.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AWSConfig {

    private final ApplicationProperties properties;

    public AWSCredentials credentials() {
        return new BasicAWSCredentials(properties.getAws().getCredential().getAccessKey(), properties.getAws().getCredential().getSecret());
    }

    @Bean
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder
            .standard()
            .withPayloadSigningEnabled(true)
            .withPathStyleAccessEnabled(true)
            .withCredentials(new AWSStaticCredentialsProvider(credentials()))
            .withRegion(properties.getAws().getRegion().getValue())
            .build();
    }
}
