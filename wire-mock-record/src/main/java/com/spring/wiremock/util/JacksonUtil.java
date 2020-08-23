package com.spring.wiremock.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JacksonUtil {
    private static final Logger LOG = LoggerFactory.getLogger(JacksonUtil.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private JacksonUtil(){
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(new JavaTimeModule());
    }

    public static String convertObjectToJson(Object object){
        try {
            if(object != null)
                return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            LOG.error("Error in converting object to json {}", e.getMessage());
        }
        return null;
    }

    public static <T> T convertJsonToObject(String jsonString, Class<T> classOfT) {
        try {
            if(jsonString != null)
                return mapper.readValue(jsonString, classOfT);
        } catch (JsonProcessingException e) {
            LOG.error("Error in converting json to object {}", e.getMessage());
        }
        return null;
    }

    public static byte[] convertObjectToJsonBytes(Object object) {
        try {
            return mapper.writeValueAsBytes(object);
        }catch (JsonProcessingException e){
            LOG.error("Error in converting object to byte {}", e.getMessage());
        }
        return null;
    }
}