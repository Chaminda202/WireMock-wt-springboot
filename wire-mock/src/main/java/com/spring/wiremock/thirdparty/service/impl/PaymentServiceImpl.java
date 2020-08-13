package com.spring.wiremock.thirdparty.service.impl;

import com.spring.wiremock.config.ThirdPartyProperties;
import com.spring.wiremock.thirdparty.model.request.PaymentRequest;
import com.spring.wiremock.thirdparty.model.response.PaymentResponse;
import com.spring.wiremock.thirdparty.service.PaymentService;
import com.spring.wiremock.util.CommonUtil;
import com.spring.wiremock.util.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PaymentServiceImpl implements PaymentService {
    private static final Logger LOG = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private final ThirdPartyProperties thirdPartyProperties;
    private final RestTemplate restTemplate;
    private String paymentUrl;
    private static final String FORWARD_SLASH = "/";

    public PaymentServiceImpl(ThirdPartyProperties thirdPartyProperties, RestTemplate restTemplate) {
        this.thirdPartyProperties = thirdPartyProperties;
        this.restTemplate = restTemplate;
        StringBuilder url = new StringBuilder();
        url.append(this.thirdPartyProperties.getBaseUrl());
        url.append(FORWARD_SLASH);
        url.append(this.thirdPartyProperties.getVersion());
        url.append(FORWARD_SLASH);
        url.append("payments");
        this.paymentUrl = url.toString();
    }

    @Override
    public PaymentResponse makePayment(PaymentRequest paymentRequest) {
        LOG.info("Start make payment third party call {}", JacksonUtil.convertObjectToJson(paymentRequest));
        HttpEntity<PaymentRequest> entity = new HttpEntity<>(paymentRequest, CommonUtil.createJsonHttpHeader());
        ResponseEntity<PaymentResponse> responseEntity = restTemplate.exchange(this.paymentUrl, HttpMethod.POST, entity,
                PaymentResponse.class);
        LOG.info("End make payment third party call {}", JacksonUtil.convertObjectToJson(responseEntity.getBody()));
        return responseEntity.getBody();
    }

    @Override
    public PaymentResponse updatePayment(PaymentRequest paymentRequest) {
        LOG.info("Start update payment third party call {}", JacksonUtil.convertObjectToJson(paymentRequest));
        HttpEntity<PaymentRequest> entity = new HttpEntity<>(paymentRequest, CommonUtil.createJsonHttpHeader());
        ResponseEntity<PaymentResponse> responseEntity = restTemplate.exchange(this.paymentUrl, HttpMethod.PUT, entity,
                PaymentResponse.class);
        LOG.info("End update payment third party call {}", JacksonUtil.convertObjectToJson(responseEntity.getBody()));
        return responseEntity.getBody();
    }
}
