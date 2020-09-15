package com.andonov.cloud.availability.util;

import com.andonov.cloud.availability.dto.CallbackCredentials;
import com.andonov.cloud.availability.dto.CallbackDTO;
import com.andonov.cloud.availability.dto.CallbackQuota;
import com.andonov.cloud.availability.dto.Credentials;
import com.andonov.cloud.availability.dto.InternalCallback;
import com.andonov.cloud.availability.dto.Quota;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CallbackUtil {

    public static CallbackDTO convert(InternalCallback c) {
        return CallbackDTO.builder()
                          .callbackId(c.getCallbackId())
                          .healthStatus(c.getHealthStatus())
                          .pingUrl(c.getPingUrl())
                          .quota(convert(c.getQuota()))
                          .credentials(convert(c.getCredentials()))
                          .build();
    }

    public static List<CallbackDTO> convert(Iterable<InternalCallback> callbacks) {
        List<CallbackDTO> callbacksResult = new ArrayList<>();
        callbacks.forEach(c -> callbacksResult.add(convert(c)));
        return callbacksResult;
    }

    public static Credentials convert(CallbackCredentials c) {
        return Credentials.builder().appName(c.getAppName()).appUri(c.getAppUri()).api(c.getApi()).org(c.getOrg()).space(c.getSpace()).user(c.getUser()).password(c.getPassword()).encryptionKey(c.getEncryptionKey()).encryptionVector(c.getEncryptionVector()).build();
    }

    public static Quota convert(CallbackQuota q) {
        return Quota.builder().instances(q.getInstances()).memory(q.getMemory()).disk(q.getDisk()).build();
    }

}
