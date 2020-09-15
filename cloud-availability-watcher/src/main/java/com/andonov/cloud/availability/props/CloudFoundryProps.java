package com.andonov.cloud.availability.props;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "cloud-foundry")
class CloudFoundryProps {

    private String appName;
    private String appUri;
    private String api;
    private String org;
    private String space;
    private String user;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private String pass;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private String encKey;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private String encVec;
}
