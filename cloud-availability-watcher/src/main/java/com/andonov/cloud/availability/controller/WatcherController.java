package com.andonov.cloud.availability.controller;

import com.andonov.cloud.availability.constants.API;
import com.andonov.cloud.availability.dto.CallbackDTO;
import com.andonov.cloud.availability.dto.Credentials;
import com.andonov.cloud.availability.dto.Quota;
import com.andonov.cloud.availability.service.CacheableService;
import com.andonov.cloud.availability.service.ResourceManager;
import com.andonov.cloud.availability.util.Executor;
import com.andonov.cloud.availability.util.RestExecutor;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = API.WATCHER_API_PATH, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class WatcherController {

    private final RestExecutor restExecutor;
    private final CacheableService cacheableService;
    private final ResourceManager resourceManager;

    @Autowired
    public WatcherController(RestExecutor restExecutor, CacheableService cacheableService, ResourceManager resourceManager) {
        this.restExecutor = restExecutor;
        this.cacheableService = cacheableService;
        this.resourceManager = resourceManager;
    }

    @GetMapping("/ping")
    public ResponseEntity<Credentials> ping(@RequestHeader("pingUrl") String url) {
        return restExecutor.execGet(url, null, Credentials.class);
    }

    @PostMapping("/scale")
    public ResponseEntity<Quota> scale(@RequestBody byte[] data) {
        CallbackDTO callbackDTO = SerializationUtils.deserialize(data);
        Credentials credentials = callbackDTO.getCredentials();

        cacheableService.login(credentials);

        String response = Executor.exec("../cf v3-scale " + credentials.getAppName());
        Quota quota = resourceManager.checkHealthStatus(credentials.getAppName(), callbackDTO.getQuota(), response);

        return ResponseEntity.ok().body(quota);
    }

}
