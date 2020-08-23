package com.spring.wiremock.thirdparty.model.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class PaymentRequest {
    private String paymentId;
    private String cardNumber;
    private String bank;
    private LocalDate cardExpiryDate;
    private BigDecimal amount;
}
