package com.netceler.project_anonymization.fileStorage;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "filestorage")
@Getter
@Setter
public class FileStorageProperties {

    private String defaultLocation;
    private String toAnonymizeLocation;
    private String anonymizedLocation;
}
