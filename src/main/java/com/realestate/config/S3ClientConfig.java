package com.realestate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
public class S3ClientConfig {

    private final CloudflareR2Properties cloudflareR2Properties;

    public S3ClientConfig(CloudflareR2Properties cloudflareR2Properties) {
        this.cloudflareR2Properties = cloudflareR2Properties;
    }

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                cloudflareR2Properties.getAccessKey(),
                cloudflareR2Properties.getSecretKey()
        );

        S3Configuration s3Configuration = S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .chunkedEncodingEnabled(false) // Important for R2
                .build();

        return S3Client.builder()
                .endpointOverride(URI.create(cloudflareR2Properties.getEndpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.US_EAST_1) // Use US_EAST_1 for better R2 compatibility
                .serviceConfiguration(s3Configuration)
                .build();
    }
}
