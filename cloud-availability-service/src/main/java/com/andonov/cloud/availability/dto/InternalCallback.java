package com.andonov.cloud.availability.dto;

import com.andonov.cloud.availability.util.Util;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternalCallback {

    private static final String ID_PREFIX = "callback-";

    @Id
    private String callbackId;
    private String pingUrl;

    private HealthStatus healthStatus;

    @JoinColumn(unique = true)
    @OneToOne(cascade = CascadeType.ALL)
    private CallbackQuota quota;

    @JoinColumn(unique = true)
    @OneToOne(cascade = CascadeType.ALL)
    private CallbackCredentials credentials;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    static InternalCallback create(CallbackDTO callback) {
        final LocalDateTime now = Util.currentUtcTime();
        return InternalCallback.builder()
                               .callbackId(ID_PREFIX + UUID.randomUUID())
                               .healthStatus(HealthStatus.UNKNOWN)
                               .pingUrl(callback.getPingUrl())
                               .quota(new CallbackQuota(callback.getQuota()))
                               .credentials(new CallbackCredentials(callback.getCredentials()))
                               .createdAt(now)
                               .updatedAt(now)
                               .build();
    }

    static InternalCallback update(InternalCallback currentCallback, CallbackDTO newCallback) {
        return InternalCallback.builder()
                               .callbackId(currentCallback.getCallbackId())
                               .healthStatus(newCallback.getHealthStatus())
                               .pingUrl(newCallback.getPingUrl())
                               .quota(new CallbackQuota(newCallback.getQuota()))
                               .credentials(new CallbackCredentials(newCallback.getCredentials()))
                               .createdAt(currentCallback.getCreatedAt())
                               .updatedAt(Util.currentUtcTime())
                               .build();
    }

    public InternalCallback updateHealthStatus(HealthStatus healthStatus) {
        this.setHealthStatus(healthStatus);
        return this;
    }

}
