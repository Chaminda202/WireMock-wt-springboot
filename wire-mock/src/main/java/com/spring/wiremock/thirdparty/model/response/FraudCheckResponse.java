package com.spring.wiremock.thirdparty.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FraudCheckResponse {
    private boolean blacklisted;
}
