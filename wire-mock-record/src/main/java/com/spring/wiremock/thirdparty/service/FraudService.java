package com.spring.wiremock.thirdparty.service;

import com.spring.wiremock.thirdparty.model.request.FraudCheckRequest;
import com.spring.wiremock.thirdparty.model.response.FraudCheckResponse;

public interface FraudService {
    FraudCheckResponse checkFraudStatus(FraudCheckRequest fraudCheckRequest);
}
