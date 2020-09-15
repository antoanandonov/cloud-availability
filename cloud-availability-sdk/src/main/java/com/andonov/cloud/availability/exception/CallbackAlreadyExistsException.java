package com.andonov.cloud.availability.exception;

import java.text.MessageFormat;

public class CallbackAlreadyExistsException extends Exception {

    public CallbackAlreadyExistsException(String url) {
        super(MessageFormat.format("Callback with URL: {0} already exists!", url));
    }

    public CallbackAlreadyExistsException(String app, String url) {
        super(MessageFormat.format("Callback for app: {0} with URL: {1} already exists!", app, url));
    }

}