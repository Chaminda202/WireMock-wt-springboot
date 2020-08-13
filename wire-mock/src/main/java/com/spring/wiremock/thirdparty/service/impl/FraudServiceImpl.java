package com.spring.wiremock.thirdparty.service.impl;

import com.spring.wiremock.config.ThirdPartyProperties;
import com.spring.wiremock.thirdparty.model.request.FraudCheckRequest;
import com.spring.wiremock.thirdparty.model.response.FraudCheckResponse;
import com.spring.wiremock.thirdparty.service.FraudService;
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
public class FraudServiceImpl implements FraudService {
    private static final Logger LOG = LoggerFactory.getLogger(FraudServiceImpl.class);
    private final ThirdPartyProperties thirdPartyProperties;
    private final RestTemplate restTemplate;
    private String fraudCheckUrl;
    private static final String FORWARD_SLASH = "/";

    public FraudServiceImpl(ThirdPartyProperties thirdPartyProperties, RestTemplate restTemplate) {
        this.thirdPartyProperties = thirdPartyProperties;
        this.restTemplate = restTemplate;
        StringBuilder url = new StringBuilder()
            .append(this.thirdPartyProperties.getBaseUrl())
            .append(FORWARD_SLASH)
            .append(this.thirdPartyProperties.getVersion())
            .append(FORWARD_SLASH)
            .append("fraudCheck");
        this.fraudCheckUrl = url.toString();
    }
    @Override
    public FraudCheckResponse checkFraudStatus(FraudCheckRequest fraudCheckRequest) {
        LOG.info("Start fraud check third party call {}", JacksonUtil.convertObjectToJson(fraudCheckRequest));
        HttpEntity<FraudCheckRequest> entity = new HttpEntity<>(fraudCheckRequest, CommonUtil.createJsonHttpHeader());
        ResponseEntity<FraudCheckResponse> responseEntity = restTemplate.exchange(this.fraudCheckUrl, HttpMethod.POST, entity,
                FraudCheckResponse.class);
        LOG.info("End fraud check third party call {}", JacksonUtil.convertObjectToJson(responseEntity.getBody()));
        return responseEntity.getBody();
    }
}
