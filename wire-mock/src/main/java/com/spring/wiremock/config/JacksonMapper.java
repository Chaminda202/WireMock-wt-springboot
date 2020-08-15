package com.spring.wiremock.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JacksonMapper {
    private static final Logger LOG = LoggerFactory.getLogger(JacksonMapper.class);
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

    public <T> T convertJsonToObject(String jsonString, Class<T> classOfT) {
        try {
            if(jsonString != null)
                return objectMapper.readValue(jsonString, classOfT);
        } catch (JsonProcessingException e) {
            LOG.error("Error in converting json to object in JacksonMapper {}", e.getMessage());
        }
        return null;
    }

    public byte[] convertObjectToJsonBytes(Object object) {
        try {
            return objectMapper.writeValueAsBytes(object);
        }catch (JsonProcessingException e){
            LOG.error("Error in converting object to byte in JacksonMapper {}", e.getMessage());
        }
        return null;
    }
}
