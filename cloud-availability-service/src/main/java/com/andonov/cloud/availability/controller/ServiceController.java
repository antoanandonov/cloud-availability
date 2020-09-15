package com.andonov.cloud.availability.controller;

import com.andonov.cloud.availability.constants.API;
import com.andonov.cloud.availability.dto.CallbackDTO;
import com.andonov.cloud.availability.dto.CallbackRepository;
import com.andonov.cloud.availability.dto.Credentials;
import com.andonov.cloud.availability.dto.HealthStatus;
import com.andonov.cloud.availability.dto.NetworkProps;
import com.andonov.cloud.availability.exception.CallbackAlreadyExistsException;
import com.andonov.cloud.availability.props.ServiceProps;
import com.andonov.cloud.availability.util.RestExecutor;
import com.andonov.cloud.availability.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
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
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping(value = API.SERVICE_API_PATH, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ServiceController {

    private final CallbackRepository callbackRepo;
    private final RestExecutor restExecutor;
    private final ServiceProps serviceProps;

    @Autowired
    public ServiceController(CallbackRepository callbackRepo, RestExecutor restExecutor, ServiceProps serviceProps) {
        this.callbackRepo = callbackRepo;
        this.restExecutor = restExecutor;
        this.serviceProps = serviceProps;
    }

    @GetMapping(value = "/hi", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok().body("Hello, the time on the server now is: " + LocalDateTime.now() + System.lineSeparator() + System.lineSeparator() + System.getenv() + System.lineSeparator() + System.lineSeparator() + System.getProperties());
    }

    @GetMapping("/pingServer")
    public ResponseEntity<NetworkProps> pingServer() throws IOException {
        NetworkProps.NetworkPropsBuilder networkProps = NetworkProps.builder().internalIp(InetAddress.getLocalHost().getHostAddress()).hostName(InetAddress.getLocalHost().getHostName());
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new URL("http://checkip.amazonaws.com").openStream()))) {
            networkProps.externalIp(in.readLine());
        }
        return ResponseEntity.ok(networkProps.build());
    }

    @GetMapping("/callbacks")
    public ResponseEntity<List<CallbackDTO>> listAll() {
        List<CallbackDTO> callbacks = callbackRepo.list();
        return ResponseEntity.ok().body(callbacks);
    }

    @PostMapping("/encrypt")
    public ResponseEntity<byte[]> encrypt(@RequestBody byte[] data) {
        return ResponseEntity.ok().body(SerializationUtils.serialize(Util.encrypt(data, serviceProps.getEncKey(), serviceProps.getEncVec())));
    }

    @GetMapping("/callbacks/{callbackId}")
    public ResponseEntity<CallbackDTO> find(@PathVariable("callbackId") String callbackId) {
        CallbackDTO callback = callbackRepo.findById(callbackId);
        log.info("Looking for callback with ID: {}", callback.getCallbackId());
        return ResponseEntity.ok().body(callback);
    }

    @PostMapping("/callbacks")
    public ResponseEntity<?> register(@RequestBody byte[] data) throws CallbackAlreadyExistsException {
        CallbackDTO callback = SerializationUtils.deserialize(data);
        log.info("Going to register: {}", callback);

        if (restExecutor.ping(callback.getPingUrl(), null).getStatusCode().isError()) {
            return ResponseEntity.unprocessableEntity().body("Could not ping: " + callback.getPingUrl());
        }

        CallbackDTO newCallback = callbackRepo.register(callback);
        log.info("Successfully registered callback with ID: {}", newCallback.getCallbackId());

        UriComponents uriComponents = UriComponentsBuilder.fromUriString(newCallback.getPingUrl()).build();
        return ResponseEntity.created(uriComponents.toUri()).body(newCallback);
    }

    @PostMapping("/callbacksUi")
    public ResponseEntity<?> register(@RequestBody CallbackDTO callback) throws CallbackAlreadyExistsException {
        log.info("Going to register: {}", callback);

        ResponseEntity<Credentials> response = restExecutor.ping(callback.getPingUrl(), null);
        if (response.getStatusCode().isError()) {
            return ResponseEntity.unprocessableEntity().body("Could not ping: " + callback.getPingUrl());
        }

        Credentials cfCred = Optional.ofNullable(response.getBody()).orElse(Credentials.builder().build());
        Credentials callbackCred = callback.getCredentials();

        Credentials credentials = Credentials.builder().user(callbackCred.getUser()).password(callbackCred.getPassword()).appName(cfCred.getAppName()).appUri(cfCred.getAppUri()).api(cfCred.getApi()).org(cfCred.getOrg()).space(cfCred.getSpace()).build();
        callback.setCredentials(credentials);

        CallbackDTO newCallback = callbackRepo.register(callback);

        log.info("Successfully registered callback with ID: {}", newCallback.getCallbackId());

        UriComponents uriComponents = UriComponentsBuilder.fromUriString(newCallback.getPingUrl()).build();
        return ResponseEntity.created(uriComponents.toUri()).body(newCallback);
    }

    @PostMapping("/callbacks/status")
    public ResponseEntity<Void> updateStatus(@RequestHeader("pingUrl") String url, @RequestHeader("healthStatus") HealthStatus healthStatus) {
        callbackRepo.updateHealthStatusByUrl(url, healthStatus);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/callbacks/{callbackId}")
    public ResponseEntity<Void> update(@PathVariable("callbackId") String callbackId, @RequestBody CallbackDTO callback) {
        CallbackDTO updatedCallback = callbackRepo.update(callbackId, callback);
        log.info("Successfully updated callback with ID: {}", updatedCallback.getCallbackId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/callbacks/{callbackId}")
    public ResponseEntity<Void> unregister(@PathVariable("callbackId") String callbackId) {
        callbackRepo.unregister(callbackId);
        log.info("Successfully unregistered callback with ID: {}", callbackId);
        return ResponseEntity.noContent().build();
    }

}
