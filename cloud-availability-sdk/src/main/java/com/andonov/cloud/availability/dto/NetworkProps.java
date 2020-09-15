package com.andonov.cloud.availability.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NetworkProps {

    @Expose @SerializedName("internalIp")
    private String internalIp;

    @Expose @SerializedName("externalIp")
    private String externalIp;

    @Expose @SerializedName("hostName")
    private String hostName;
}
