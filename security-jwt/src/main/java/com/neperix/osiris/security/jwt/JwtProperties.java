package com.neperix.osiris.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "osiris.security.jwt")
@Data
public class JwtProperties {

    private String issuer;
    private String secret;
    private long timeToLiveInHours;
}
