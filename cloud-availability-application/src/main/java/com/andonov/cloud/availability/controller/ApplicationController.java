package com.andonov.cloud.availability.controller;

import com.andonov.cloud.availability.constants.API;
import com.andonov.cloud.availability.dto.CallbackDTO;
import com.andonov.cloud.availability.dto.Credentials;
import com.andonov.cloud.availability.props.AppProps;
import com.andonov.cloud.availability.service.ServiceExecutor;
import com.andonov.cloud.availability.util.Executor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@CrossOrigin
@RestController
@RequestMapping(value = API.APP_API_PATH, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ApplicationController {

    private final ServiceExecutor serviceExecutor;
    private final AppProps appProps;

    @Autowired
    public ApplicationController(ServiceExecutor serviceExecutor, AppProps appProps) {
        this.serviceExecutor = serviceExecutor;
        this.appProps = appProps;
    }

    @GetMapping(value = "/hi", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity hello() {
        return ResponseEntity.ok().body("Hello, the time on the server now is: " + LocalDateTime.now() + System.lineSeparator());
    }

    @PostMapping("/register")
    public ResponseEntity register() {
        return serviceExecutor.registerCallback();
    }

    @GetMapping("/exec")
    public ResponseEntity cmd(@RequestHeader("cmd") String cmd) {
        Executor.exec(cmd);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/ping")
    public ResponseEntity<Credentials> ping() {
        Credentials cfCredentials = Credentials.builder()
                                               .appName(appProps.getAppName())
                                               .appUri(appProps.getAppUri())
                                               .api(appProps.getApi())
                                               .org(appProps.getOrg())
                                               .space(appProps.getSpace())
                                               .build();

        return ResponseEntity.ok(cfCredentials);
    }

    @PatchMapping("/update/{callbackId}")
    public ResponseEntity update(@PathVariable("callbackId") String callbackId, @RequestBody CallbackDTO callback) {
        return serviceExecutor.updateCallback(callbackId, callback);
    }

    @DeleteMapping("/unregister/{callbackId}")
    public ResponseEntity unregister(@PathVariable("callbackId") String callbackId) {
        serviceExecutor.unregisterCallback(callbackId);
        System.out.println("Successfully unregistered callback with ID: " + callbackId);
        return ResponseEntity.noContent().build();
    }

}
