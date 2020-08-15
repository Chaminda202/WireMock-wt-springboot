package com.spring.wiremock.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.wiremock.util.JacksonUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JacksonMapper {
    private static final Logger LOG = LoggerFactory.getLogger(JacksonUtil.class);
    private final ObjectMapper objectMapper;

    public String convertObjectToJson(Object object){
        try {
            if(object != null)
                return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            LOG.error("Error in converting object to json in JacksonMapper {}", e.getMessage());
        }
        return null;
    }
}
