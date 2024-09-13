package com.jm.futelove.credentials;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@Data
@Component
@ComponentScan
@ConfigurationProperties(prefix = "google.cloud")
public class GoogleCloudProperties {

    private int connectionTimeout = 60000;
    private int readTimeout = 60000;
    private String securityKeyFile;
    private String projectId;
    private String location;

}
