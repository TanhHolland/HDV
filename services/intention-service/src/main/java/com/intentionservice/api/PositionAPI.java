package com.intentionservice.api;

import com.intentionservice.domain.vo.DriverStatusVo;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class PositionAPI
{
    @Autowired
    private RestTemplate restTemplate;

    @CircuitBreaker(name = "positionService", fallbackMethod = "defaultMatch")
    public Collection<DriverStatusVo> match(double longitude, double latitude)
    {
        ResponseEntity<Collection<DriverStatusVo>> matchResponse = restTemplate.exchange(
                "http://QBIKE-POSITION/api/position/match?longitude=" + longitude + "&latitude=" + latitude,
                HttpMethod.GET, null,
                new ParameterizedTypeReference<Collection<DriverStatusVo>>() {
                });
        return matchResponse.getBody();
    }

    public Collection<DriverStatusVo> defaultMatch(double longitude, double latitude)
    {
        return new ArrayList<>();
    }
}