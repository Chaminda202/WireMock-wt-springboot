package com.spring.wiremock.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.spring.wiremock.config.JacksonMapper;
import com.spring.wiremock.config.ThirdPartyProperties;
import com.spring.wiremock.enumeration.PaymentStatusEnum;
import com.spring.wiremock.model.request.BookingPaymentRequest;
import com.spring.wiremock.model.request.CardDetails;
import com.spring.wiremock.model.response.BookingPaymentResponse;
import com.spring.wiremock.service.impl.BookingPaymentServiceImpl;
import com.spring.wiremock.thirdparty.model.request.PaymentRequest;
import com.spring.wiremock.thirdparty.model.response.PaymentResponse;
import com.spring.wiremock.thirdparty.service.FraudService;
import com.spring.wiremock.thirdparty.service.PaymentService;
import com.spring.wiremock.thirdparty.service.impl.FraudServiceImpl;
import com.spring.wiremock.thirdparty.service.impl.PaymentServiceImpl;
import com.spring.wiremock.util.Constants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class BookingPaymentServiceWithSingleStubbingTest {
    @Autowired
    private ThirdPartyProperties thirdPartyProperties;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private JacksonMapper jacksonMapper;
    private BookingPaymentService bookingPaymentService;

    private WireMockServer wireMockServer;


    @BeforeEach
    void setup() {
        this.wireMockServer = new WireMockServer(this.thirdPartyProperties.getPort());
        //configureFor(this.thirdPartyProperties.getHost(), this.thirdPartyProperties.getPort());
        this.wireMockServer.start();
        PaymentService paymentService = new PaymentServiceImpl(thirdPartyProperties, restTemplate, jacksonMapper);
        FraudService fraudService = new FraudServiceImpl(thirdPartyProperties, restTemplate);
        this.bookingPaymentService = new BookingPaymentServiceImpl(paymentService, fraudService, thirdPartyProperties);
    }

    /***
     * Single third party call mocking using wire mock
     */
    @Test
    void makePaymentTest() {

        BookingPaymentRequest request = BookingPaymentRequest.builder()
                .bookingId("1234")
                .amount(new BigDecimal("2500"))
                .cardDetails(CardDetails.builder()
                        .bank("UOB")
                        .expiry(LocalDate.of(2024, 10, 25))
                        .number("1234-3245-4325-1289")
                        .build())
                .build();

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .paymentId("2020081423104000067")
                .cardNumber(request.getCardDetails().getNumber())
                .bank(request.getCardDetails().getBank())
                .cardExpiryDate(request.getCardDetails().getExpiry())
                .amount(request.getAmount())
                .build();

        PaymentResponse expectedPaymentResponse = PaymentResponse.builder()
                .paymentId(paymentRequest.getPaymentId())
                .status(PaymentStatusEnum.SUCCESS)
                .build();

        //given
        StringBuilder updateUrlPath = new StringBuilder(Constants.FORWARD_SLASH)
                .append(this.thirdPartyProperties.getVersion())
                .append("/payments");


        this.wireMockServer.stubFor(put(urlPathEqualTo(updateUrlPath.toString()))
                .withRequestBody(equalToJson(this.jacksonMapper.convertObjectToJson(paymentRequest)))
                .willReturn(okJson(this.jacksonMapper.convertObjectToJson(expectedPaymentResponse)))
        );

        // when
        BookingPaymentResponse response = this.bookingPaymentService.updatePayment(request, "2020081423104000067");

        //then
        assertThat(response.getBookingId()).isEqualTo(request.getBookingId());
        assertThat(response.getPaymentId()).isEqualTo(paymentRequest.getPaymentId());
        assertThat(response.getStatus()).isEqualTo(PaymentStatusEnum.SUCCESS);
    }

    @AfterEach
    void tearDown() {
        this.wireMockServer.stop();
    }
}
