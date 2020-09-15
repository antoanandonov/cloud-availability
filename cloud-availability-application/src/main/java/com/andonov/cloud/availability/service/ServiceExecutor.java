package com.andonov.cloud.availability.service;

import com.andonov.cloud.availability.constants.API;
import com.andonov.cloud.availability.dto.CallbackDTO;
import com.andonov.cloud.availability.dto.Credentials;
import com.andonov.cloud.availability.dto.Quota;
import com.andonov.cloud.availability.props.AppProps;
import com.andonov.cloud.availability.util.RestExecutor;
import com.andonov.cloud.availability.util.Util;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class ServiceExecutor {

    private static final String SLASH = "/";
    private final RestExecutor restExecutor;
    private final AppProps appProps;

    @Autowired
    public ServiceExecutor(RestExecutor restExecutor, AppProps appProps) {
        this.restExecutor = restExecutor;
        this.appProps = appProps;
    }

    public ResponseEntity registerCallback() {
        Quota quota = Quota.builder().instances(1).memory("1G").disk("1G").build();
        CallbackDTO callbackForRegistration = CallbackDTO.builder()
                                                         .pingUrl(API.APPLICATION_PING_URL)
                                                         .quota(quota)
                                                         .credentials(buildCredentials())
                                                         .build();

        return restExecutor.execPost(appProps.getServiceUrlForRegistration(), new HttpEntity<>(SerializationUtils.serialize(callbackForRegistration)), CallbackDTO.class);
    }

    public ResponseEntity updateCallback(String callbackId, CallbackDTO callbackDTO) {
        return restExecutor.execPatch(appProps.getServiceUrlForRegistration() + SLASH + callbackId, new HttpEntity<>(callbackDTO), CallbackDTO.class);
    }

    public void unregisterCallback(String callbackId) {
        restExecutor.execDelete(UriComponentsBuilder.fromUriString(appProps.getServiceUrlForRegistration() + SLASH + callbackId).build().toUri());
    }

    private Credentials buildCredentials() {
        byte[] pass = SerializationUtils.serialize(Util.encrypt(appProps.getPass().toCharArray(), appProps.getEncKey(), appProps.getEncVec()));
        return Credentials.builder()
                          .appName(appProps.getAppName())
                          .appUri(appProps.getAppUri())
                          .api(appProps.getApi())
                          .org(appProps.getOrg())
                          .space(appProps.getSpace())
                          .user(appProps.getUser())
                          .password(pass)
                          .encryptionKey(appProps.getEncKey())
                          .encryptionVector(appProps.getEncVec())
                          .build();
    }
}
