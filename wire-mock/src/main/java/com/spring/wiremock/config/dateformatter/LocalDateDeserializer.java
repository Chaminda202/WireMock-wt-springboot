package com.spring.wiremock.config.dateformatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.spring.wiremock.config.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {
    private final ApplicationProperties applicationProperties;

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext context) throws IOException {
        return LocalDate.parse(p.getValueAsString(), DateTimeFormatter.ofPattern(this.applicationProperties.getLocalDateFormat()));
    }
}