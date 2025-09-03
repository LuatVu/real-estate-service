package com.realestate.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cloudflare.r2")
@Data
@ToString(exclude = "secretKey")
public class CloudflareR2Properties {    
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucket;
}
