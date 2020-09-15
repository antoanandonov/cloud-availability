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
public class Quota implements Serializable {

    private static final long serialVersionUID = 1L;

    @Expose @SerializedName("instances")
    private Integer instances;

    @Expose @SerializedName("memory")
    private String memory;

    @Expose @SerializedName("disk")
    private String disk;

}
