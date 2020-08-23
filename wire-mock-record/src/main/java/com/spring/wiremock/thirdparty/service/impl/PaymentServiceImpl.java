package com.spring.wiremock.thirdparty.service.impl;

import com.spring.wiremock.config.JacksonMapper;
import com.spring.wiremock.config.ThirdPartyProperties;
import com.spring.wiremock.thirdparty.model.request.PaymentRequest;
import com.spring.wiremock.thirdparty.model.response.PaymentResponse;
import com.spring.wiremock.thirdparty.service.PaymentService;
import com.spring.wiremock.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class PaymentServiceImpl implements PaymentService {
    private static final Logger LOG = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private final ThirdPartyProperties thirdPartyProperties;
    private final RestTemplate restTemplate;
    private final JacksonMapper jacksonMapper;
    private String paymentUrl;

    public PaymentServiceImpl(ThirdPartyProperties thirdPartyProperties, RestTemplate restTemplate, JacksonMapper jacksonMapper) {
        this.thirdPartyProperties = thirdPartyProperties;
        this.restTemplate = restTemplate;
        this.jacksonMapper = jacksonMapper;

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme(this.thirdPartyProperties.getProtocol())
                .host(this.thirdPartyProperties.getHost())
                .port(this.thirdPartyProperties.getPort())
                .path(this.thirdPartyProperties.getVersion() + "/payments")
                .build();
        this.paymentUrl = uriComponents.toString();
    }

    @Override
    public PaymentResponse makePayment(PaymentRequest paymentRequest) {
        LOG.info("Start make payment third party call {}", this.jacksonMapper.convertObjectToJson(paymentRequest));
        HttpEntity<String> entity = new HttpEntity<>(this.jacksonMapper.convertObjectToJson(paymentRequest), CommonUtil.createJsonHttpHeader());
        ResponseEntity<PaymentResponse> responseEntity = restTemplate.exchange(this.paymentUrl, HttpMethod.POST, entity,
                PaymentResponse.class);
        LOG.info("End make payment third party call {}", this.jacksonMapper.convertObjectToJson(responseEntity.getBody()));
        return responseEntity.getBody();
    }

    @Override
    public PaymentResponse updatePayment(PaymentRequest paymentRequest) {
        LOG.info("Start update payment third party call {}", this.jacksonMapper.convertObjectToJson(paymentRequest));
        HttpEntity<String> entity = new HttpEntity<>(this.jacksonMapper.convertObjectToJson(paymentRequest), CommonUtil.createJsonHttpHeader());
        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(this.paymentUrl + "/{paymentId}")
                .buildAndExpand(paymentRequest.getPaymentId());
        paymentRequest.setPaymentId(null);
        ResponseEntity<PaymentResponse> responseEntity = restTemplate.exchange(uriComponents.toString(), HttpMethod.PUT, entity,
                PaymentResponse.class);
        LOG.info("End update payment third party call {}", this.jacksonMapper.convertObjectToJson(responseEntity.getBody()));
        return responseEntity.getBody();
    }
}
