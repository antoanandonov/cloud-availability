package com.andonov.cloud.availability.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Credentials implements Serializable {

    private static final long serialVersionUID = 1L;

    @Expose @SerializedName("appName")
    private String appName;

    @Expose @SerializedName("appUri")
    private String appUri;

    @Expose @SerializedName("api")
    private String api;

    @Expose @SerializedName("org")
    private String org;

    @Expose @SerializedName("space")
    private String space;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Expose(serialize = false) @SerializedName("user")
    private String user;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Expose(serialize = false) @SerializedName("password")
    private byte[] password;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Expose(serialize = false) @SerializedName("encryptionKey")
    private String encryptionKey;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Expose(serialize = false) @SerializedName("encryptionVector")
    private String encryptionVector;

}
