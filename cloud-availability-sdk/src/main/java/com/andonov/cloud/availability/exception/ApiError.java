package com.andonov.cloud.availability.exception;

import com.andonov.cloud.availability.adapter.LocalDateTimeAdapter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@ToString
public class ApiError {

    @JsonAdapter(LocalDateTimeAdapter.class)
    @Expose @SerializedName("timestamp")
    private final LocalDateTime timestamp;

    @Expose @SerializedName("status")
    private final int status;

    @Expose @SerializedName("error")
    private final String error;

    @Expose @SerializedName("message")
    private final String message;

    @Expose @SerializedName("cause")
    private final String cause;

    @Expose @SerializedName("path")
    private final String path;

    public ApiError(HttpStatus httpStatus, String exceptionMessage, String path) {
        this(httpStatus, exceptionMessage, null, path);
    }

    public ApiError(HttpStatus httpStatus, String exceptionMsg, String causeMsg, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = httpStatus.value();
        this.error = httpStatus.getReasonPhrase();
        this.message = exceptionMsg;
        this.cause = causeMsg;
        this.path = path;
    }

}
