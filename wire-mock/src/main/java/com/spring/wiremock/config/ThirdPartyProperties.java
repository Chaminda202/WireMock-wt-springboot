package com.spring.wiremock.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@ConfigurationProperties(prefix = "third-party", ignoreUnknownFields = false)
@Data
public class ThirdPartyProperties {
    private String baseUrl;
    private String version;
    private BigDecimal traceHolder;
}