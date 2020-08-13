package com.spring.wiremock.model.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class BookingPaymentRequest {
    private String bookingId;
    private BigDecimal amount;
    private CardDetails cardDetails;
}
