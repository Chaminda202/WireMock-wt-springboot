package com.spring.wiremock.model.response;

import com.spring.wiremock.enumeration.PaymentStatusEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BookingPaymentResponse {
    private String bookingId;
    private String paymentId;
    private PaymentStatusEnum status;
}
