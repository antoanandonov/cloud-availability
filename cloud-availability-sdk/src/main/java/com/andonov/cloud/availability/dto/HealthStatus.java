package com.andonov.cloud.availability.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum HealthStatus {
    OK("Ok"),
    WARNING("Warning"),
    CRITICAL("Critical"),
    UNSTABLE("Unstable"),
    NOT_FOUND("Not Found"),
    NOT_AVAILABLE("Not Available"),
    UNKNOWN("Unknown");

    @Getter private final String healthStatus;

}
