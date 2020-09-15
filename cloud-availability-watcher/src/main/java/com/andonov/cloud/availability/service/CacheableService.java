package com.andonov.cloud.availability.service;

import com.andonov.cloud.availability.dto.Credentials;
import com.andonov.cloud.availability.props.CloudFoundryCli;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Slf4j
@Component
public class CacheableService {

    private final CloudFoundryCli cloudFoundryCli;

    @Autowired
    public CacheableService(CloudFoundryCli cloudFoundryCli) {
        this.cloudFoundryCli = cloudFoundryCli;
    }

    @Cacheable(value = "cache")
    public void login(Credentials credentials) {
        log.debug("Caching credentials: " + credentials);
        cloudFoundryCli.login(credentials);
    }

    @CacheEvict(value = "cache", allEntries = true)
    public void evictCache() {
        log.info("Cache evicted");
    }
}
