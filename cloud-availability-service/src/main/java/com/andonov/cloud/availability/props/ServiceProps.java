package com.andonov.cloud.availability.props;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Data
@Component
@ConfigurationProperties(prefix = "service-props")
public class ServiceProps {
    private String encKey;
    private String encVec;
}
