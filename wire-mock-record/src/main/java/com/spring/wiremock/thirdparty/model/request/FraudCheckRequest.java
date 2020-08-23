package com.spring.wiremock.thirdparty.model.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FraudCheckRequest {
    private String cardNumber;
    private String bank;
}
