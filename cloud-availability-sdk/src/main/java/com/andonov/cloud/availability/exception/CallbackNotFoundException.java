package com.andonov.cloud.availability.exception;

import java.text.MessageFormat;

public class CallbackNotFoundException extends RuntimeException {

    public CallbackNotFoundException(String id) {
        super(MessageFormat.format("Callback with id {0} not found", id));
    }

}
