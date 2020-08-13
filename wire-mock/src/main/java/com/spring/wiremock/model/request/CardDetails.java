package com.spring.wiremock.model.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class CardDetails {
    private String number;
    private String bank;
    private LocalDate expiry;
}
