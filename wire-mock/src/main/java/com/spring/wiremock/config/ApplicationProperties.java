package com.spring.wiremock.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.application", ignoreUnknownFields = true) // set true to ignore unknown values eg: name
@Data
public class ApplicationProperties {
    private String localDateFormat;
}