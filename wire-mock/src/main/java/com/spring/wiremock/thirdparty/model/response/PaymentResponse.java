package com.spring.wiremock.thirdparty.model.response;

import com.spring.wiremock.enumeration.PaymentStatusEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PaymentResponse {
    private String paymentId;
    private PaymentStatusEnum status;
}
