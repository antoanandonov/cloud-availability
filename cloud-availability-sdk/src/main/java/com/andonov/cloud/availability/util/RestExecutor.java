package com.andonov.cloud.availability.util;

import com.andonov.cloud.availability.dto.Credentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Slf4j
@Component
public class RestExecutor {

    private final RestTemplate restTemplate;

    public RestExecutor() {
        this.restTemplate = new RestTemplate();
    }

    public <T> ResponseEntity<T> execGet(final String url, HttpEntity request, Class<T> cl) {
        return exec(HttpMethod.GET, url, request, cl);
    }

    public <T> ResponseEntity<T> execPost(final String url, HttpEntity request, Class<T> cl) {
        return exec(HttpMethod.POST, url, request, cl);
    }

    public <T> ResponseEntity<T> execPatch(final String url, HttpEntity request, Class<T> cl) {
        return exec(HttpMethod.PATCH, url, request, cl);
    }

    public <T> ResponseEntity<T> execDelete(final String url, Class<T> cl) {
        return exec(HttpMethod.DELETE, url, null, cl);
    }

    public void execDelete(final URI uri) {
        try {
            restTemplate.delete(uri);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public <T> ResponseEntity<T> exec(HttpMethod method, final String url, HttpEntity<?> requestEntity, Class<T> cl) {
        try {
            return restTemplate.exchange(url, method, requestEntity, cl);
        } catch (RestClientResponseException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(e.getRawStatusCode()).build();
        }
    }

    public <T> ResponseEntity<T> exec(HttpMethod method, final String url, HttpEntity<?> requestEntity, ParameterizedTypeReference<T> cl) {
        try {
            return restTemplate.exchange(url, method, requestEntity, cl);
        } catch (RestClientResponseException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(e.getRawStatusCode()).build();
        }
    }

    public ResponseEntity<Credentials> ping(String pingUrl, HttpEntity request) {
        try {
            ResponseEntity<Credentials> response = exec(HttpMethod.GET, pingUrl, request, Credentials.class);
            log.debug("Ping response: {}", response.getBody());
            return response;
        } catch (HttpClientErrorException e) {
            log.error("The Service could not ping the callback. {}", e.getMessage());
            return null;
        }

    }

}
