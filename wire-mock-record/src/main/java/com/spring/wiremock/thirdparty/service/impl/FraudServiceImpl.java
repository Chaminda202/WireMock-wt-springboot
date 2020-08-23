package com.spring.wiremock.thirdparty.service.impl;

import com.spring.wiremock.config.JacksonMapper;
import com.spring.wiremock.config.ThirdPartyProperties;
import com.spring.wiremock.thirdparty.model.request.FraudCheckRequest;
import com.spring.wiremock.thirdparty.model.response.FraudCheckResponse;
import com.spring.wiremock.thirdparty.service.FraudService;
import com.spring.wiremock.util.CommonUtil;
import com.spring.wiremock.util.Constants;
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
    private final JacksonMapper jacksonMapper;
    private String fraudCheckUrl;

    public FraudServiceImpl(ThirdPartyProperties thirdPartyProperties, RestTemplate restTemplate, JacksonMapper jacksonMapper) {
        this.thirdPartyProperties = thirdPartyProperties;
        this.restTemplate = restTemplate;
        this.jacksonMapper = jacksonMapper;
        StringBuilder url = new StringBuilder() // should be refactor way of url building. Cos duplicate the code
            .append(this.thirdPartyProperties.getProtocol())
            .append(Constants.COLON)
            .append(Constants.DOUBLE_FORWARD_SLASH)
            .append(this.thirdPartyProperties.getHost())
            .append(Constants.COLON)
            .append(this.thirdPartyProperties.getPort())
            .append(Constants.FORWARD_SLASH)
            .append(this.thirdPartyProperties.getVersion())
            .append(Constants.FORWARD_SLASH)
            .append("fraudCheck");
        this.fraudCheckUrl = url.toString();
    }
    @Override
    public FraudCheckResponse checkFraudStatus(FraudCheckRequest fraudCheckRequest) {
        LOG.info("Start fraud check third party call {}", this.jacksonMapper.convertObjectToJson(fraudCheckRequest));
        HttpEntity<String> entity = new HttpEntity<>(this.jacksonMapper.convertObjectToJson(fraudCheckRequest), CommonUtil.createJsonHttpHeader());
        ResponseEntity<FraudCheckResponse> responseEntity = restTemplate.exchange(this.fraudCheckUrl, HttpMethod.POST, entity,
                FraudCheckResponse.class);
        LOG.info("End fraud check third party call {}", this.jacksonMapper.convertObjectToJson(responseEntity.getBody()));
        return responseEntity.getBody();
    }
}
