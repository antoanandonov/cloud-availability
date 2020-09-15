package com.andonov.cloud.availability;

import com.andonov.cloud.availability.dto.CallbackDTO;
import com.andonov.cloud.availability.dto.CallbackRepository;
import com.andonov.cloud.availability.dto.Credentials;
import com.andonov.cloud.availability.dto.HealthStatus;
import com.andonov.cloud.availability.dto.Quota;
import com.andonov.cloud.availability.util.RestExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import javax.annotation.ManagedBean;
import javax.servlet.ServletContext;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
@ManagedBean
public class StartServerInitializer implements ServletContextInitializer {

    private final CallbackRepository callbackRepo;
    private final RestExecutor restExecutor;

    @Autowired
    public StartServerInitializer(CallbackRepository callbackRepo, RestExecutor restExecutor) {
        this.callbackRepo = callbackRepo;
        this.restExecutor = restExecutor;
    }

    @Override
    public void onStartup(ServletContext servletContext) {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new HealthChecker(), 0, 10, TimeUnit.SECONDS);
    }

    private class HealthChecker implements Runnable {

        private static final String MIRROR_PING_URL = "https://cloud-availability-watcher.cfapps.eu10.hana.ondemand.com/watcher/api/v1/ping";
        private static final String SCALE_URL = "https://cloud-availability-watcher.cfapps.eu10.hana.ondemand.com/watcher/api/v1/scale";
        private static final String RETURN_STATUS_PATTERN = "Will return status {}";
        private static final int MAX_PINGS = 10;
        HttpHeaders headers = new HttpHeaders();

        @Override
        public void run() {
            try {
                callbackRepo.list().stream().filter(Objects::nonNull).forEach(c -> {
                    headers.set("pingUrl", c.getPingUrl());
                    HealthStatus overallStatus = HealthStatus.UNKNOWN;
                    try {
                        List<Pair<Duration, HealthStatus>> pingSeries = pingSeries(c);
                        pingSeries.add(calculateHealthStatus(Pair.of(MIRROR_PING_URL, new HttpEntity<>(headers)), c.getHealthStatus()));

                        overallStatus = calculateOverallStatus(pingSeries);
                        log.info("Calculated status is: {}", overallStatus);

                        callbackRepo.updateHealthStatusById(c.getCallbackId(), overallStatus);
                    } catch (RestClientException e) {
                        log.debug("Catching error while trying to ping: {} " + c.getPingUrl());
                        final HealthStatus health = c.getHealthStatus();
                        final HealthStatus errorStatus = health == HealthStatus.NOT_FOUND || health == HealthStatus.NOT_AVAILABLE ? health : HealthStatus.CRITICAL;
                        callbackRepo.updateHealthStatusById(c.getCallbackId(), errorStatus);

                        callbackRepo.update(c.getCallbackId(), c);
                    }

                    if (overallStatus != HealthStatus.OK) {
                        log.error("Now the resources look like: {} .Will try to increase the necessary resources.", c.getQuota());
                        ResponseEntity<Quota> newQuota = restExecutor.execPost(SCALE_URL, new HttpEntity<>(SerializationUtils.serialize(c)), Quota.class);
                        c.setQuota(newQuota.getBody());
                        log.error("The new Quota is: {}", c.getQuota());
                        callbackRepo.update(c.getCallbackId(), c);
                    }
                });
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

        public List<Pair<Duration, HealthStatus>> pingSeries(CallbackDTO callbackDTO) {
            return IntStream.rangeClosed(1, MAX_PINGS)
                            .mapToObj(i -> calculateHealthStatus(Pair.of(callbackDTO.getPingUrl(), null), callbackDTO.getHealthStatus()))
                            .collect(Collectors.toList());
        }

        public Pair<Duration, HealthStatus> calculateHealthStatus(Pair<String, HttpEntity<?>> pingTuple, HealthStatus previousStatus) {
            try {
                LocalTime startSinglePing = LocalTime.now();
                ResponseEntity<Credentials> response = restExecutor.ping(pingTuple.getLeft(), pingTuple.getRight());
                LocalTime endSinglePing = LocalTime.now();

                Duration pingResponse = Duration.between(startSinglePing, endSinglePing);
                return Pair.of(pingResponse, calculatePingStatus(response));
            } catch (RestClientException e) {
                Set<HealthStatus> notRelevantStatuses = Stream.of(HealthStatus.NOT_FOUND, HealthStatus.NOT_AVAILABLE, HealthStatus.UNKNOWN).collect(Collectors.toSet());
                HealthStatus status = notRelevantStatuses.contains(previousStatus) ? previousStatus : HealthStatus.CRITICAL;
                log.error("Cached error and returning status {}", status, e);
                return Pair.of(Duration.ZERO, status);
            }
        }

        public HealthStatus calculatePingStatus(ResponseEntity<Credentials> ping) {
            final HttpStatus pingStatus = ping.getStatusCode();
            if (Objects.isNull(ping.getBody()) || ping.getStatusCode().is4xxClientError()) {
                log.info(RETURN_STATUS_PATTERN, HealthStatus.NOT_FOUND);
                return HealthStatus.NOT_FOUND;
            } else if (pingStatus.is2xxSuccessful()) {
                log.info(RETURN_STATUS_PATTERN, HealthStatus.OK);
                return HealthStatus.OK;
            } else if (pingStatus.is5xxServerError()) {
                log.info(RETURN_STATUS_PATTERN, HealthStatus.CRITICAL);
                return HealthStatus.CRITICAL;
            } else {
                log.info(RETURN_STATUS_PATTERN, HealthStatus.NOT_AVAILABLE);
                return HealthStatus.NOT_AVAILABLE;
            }
        }

        public HealthStatus calculateOverallStatus(List<Pair<Duration, HealthStatus>> pingSeries) {
            long failedPings = pingSeries.stream().filter(x -> Objects.isNull(x.getRight()) || x.getRight() != HealthStatus.OK).count();
            long slowPings = pingSeries.stream().filter(x -> x.getLeft().toMillis() >= Duration.ofMillis(1500).toMillis()).count();

            double slowPercentage = calculatePercentage(slowPings, pingSeries.size());
            double failPercentage = calculatePercentage(failedPings, pingSeries.size());
            double mixedPercentage = calculatePercentage((failedPings + slowPings), pingSeries.size());

            log.info("Slow %: {}, Failed %: {}, Mixed %: {}", slowPercentage, failPercentage, mixedPercentage);
            log.info("Slow: {}, Failed: {}, All: {}", slowPings, failedPings, pingSeries.size());

            if (mixedPercentage >= 50 && slowPercentage >= 50 || failPercentage >= 50) {
                return HealthStatus.CRITICAL;
            } else if ((mixedPercentage >= 25 && mixedPercentage < 80) && (slowPercentage >= 25 && slowPercentage < 50) || (failPercentage >= 25 && failPercentage < 50)) {
                return HealthStatus.WARNING;
            } else if ((mixedPercentage > 10 && mixedPercentage < 30) && (slowPercentage > 10 && slowPercentage < 25) || (failPercentage > 10 && failPercentage < 25)) {
                return HealthStatus.UNSTABLE;
            } else {
                return HealthStatus.OK;
            }
        }

        public double calculatePercentage(double obtained, double total) {
            return obtained * 100 / total;
        }

    }
}
