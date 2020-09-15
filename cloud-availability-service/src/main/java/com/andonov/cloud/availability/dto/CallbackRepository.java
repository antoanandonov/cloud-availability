package com.andonov.cloud.availability.dto;

import com.andonov.cloud.availability.exception.CallbackAlreadyExistsException;
import com.andonov.cloud.availability.exception.CallbackNotFoundException;
import com.andonov.cloud.availability.facade.Repo;
import com.andonov.cloud.availability.util.CallbackUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CallbackRepository {

    private final Repo repo;

    @Autowired
    public CallbackRepository(Repo repo) {
        this.repo = repo;
    }

    public CallbackDTO register(CallbackDTO callback) throws CallbackAlreadyExistsException {
        Credentials callbackCredentials = callback.getCredentials();
        if (Optional.ofNullable(repo.findInternalCallbackByPingUrl(callback.getPingUrl())).isPresent()) {
            throw new CallbackAlreadyExistsException(callbackCredentials.getAppName(), callback.getPingUrl());
        }
        return CallbackUtil.convert(repo.save(InternalCallback.create(callback)));
    }

    public void unregister(String callbackId) {
        repo.delete(findCallbackByCallbackId(callbackId));
    }

    public CallbackDTO update(final String callbackId, CallbackDTO newCallback) {
        return CallbackUtil.convert(repo.save(InternalCallback.update(findCallbackByCallbackId(callbackId), newCallback)));
    }

    public CallbackDTO findById(final String callbackId) {
        return CallbackUtil.convert(findCallbackByCallbackId(callbackId));
    }

    public List<CallbackDTO> list() {
        return CallbackUtil.convert(repo.findAll());
    }

    public void updateHealthStatusById(String callbackId, HealthStatus healthStatus) {
        repo.save(repo.findInternalCallbackByCallbackId(callbackId).updateHealthStatus(healthStatus));
    }

    public void updateHealthStatusByUrl(String pingUrl, HealthStatus healthStatus) {
        repo.save(repo.findInternalCallbackByPingUrl(pingUrl).updateHealthStatus(healthStatus));
    }

    private InternalCallback findCallbackByCallbackId(final String callbackId) {
        return repo.findById(callbackId).orElseThrow(() -> new CallbackNotFoundException(callbackId));
    }

}
