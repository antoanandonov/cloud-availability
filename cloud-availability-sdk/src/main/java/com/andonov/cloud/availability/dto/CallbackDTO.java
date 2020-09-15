package com.andonov.cloud.availability.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CallbackDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Expose @SerializedName("callbackId")
    private String callbackId;

    @Expose @SerializedName("healthStatus")
    private HealthStatus healthStatus;

    @Expose @SerializedName("pingUrl")
    private String pingUrl;

    @Expose @SerializedName("quota")
    private Quota quota;

    @Expose @SerializedName("credentials")
    private Credentials credentials;

}
