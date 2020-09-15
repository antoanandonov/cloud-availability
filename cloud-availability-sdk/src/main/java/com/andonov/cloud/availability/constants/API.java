package com.andonov.cloud.availability.constants;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public final class API {

    private static final String BASE_URL = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
    public static final String PING = "/ping";
    public static final String CALLBACKS = "/callbacks";

    public static final String APP_API_PATH = "/application/api/v1";
    public static final String WATCHER_API_PATH = "/watcher/api/v1";
    public static final String APPLICATION_PING_URL = BASE_URL + APP_API_PATH + PING;

    public static final String SERVICE_API_PATH = "/service/api/v1";

}
