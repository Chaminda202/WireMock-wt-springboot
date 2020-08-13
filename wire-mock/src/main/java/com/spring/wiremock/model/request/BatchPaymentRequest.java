package com.spring.wiremock.model.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class BatchPaymentRequest {
    private List<BookingPaymentRequest> paymentRequests;
}
