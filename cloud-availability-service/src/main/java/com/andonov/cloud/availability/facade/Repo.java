package com.andonov.cloud.availability.facade;

import com.andonov.cloud.availability.dto.InternalCallback;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Repo extends CrudRepository<InternalCallback, String> {

    InternalCallback findInternalCallbackByCallbackId(String callbackId);

    InternalCallback findInternalCallbackByPingUrl(String pingUri);

}
