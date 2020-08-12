package com.spring.wiremock.thirdparty.service;

import com.spring.wiremock.thirdparty.model.request.PaymentRequest;
import com.spring.wiremock.thirdparty.model.response.PaymentResponse;

public interface PaymentService {
    PaymentResponse makePayment(PaymentRequest paymentRequest);
    PaymentResponse updatePayment(PaymentRequest paymentRequest);
}
