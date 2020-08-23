package com.spring.wiremock.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.recording.RecordSpec;
import com.spring.wiremock.config.JacksonMapper;
import com.spring.wiremock.config.ThirdPartyProperties;
import com.spring.wiremock.enumeration.PaymentStatusEnum;
import com.spring.wiremock.model.request.BookingPaymentRequest;
import com.spring.wiremock.model.request.CardDetails;
import com.spring.wiremock.model.response.BookingPaymentResponse;
import com.spring.wiremock.service.impl.BookingPaymentServiceImpl;
import com.spring.wiremock.thirdparty.model.request.FraudCheckRequest;
import com.spring.wiremock.thirdparty.model.request.PaymentRequest;
import com.spring.wiremock.thirdparty.model.response.FraudCheckResponse;
import com.spring.wiremock.thirdparty.service.FraudService;
import com.spring.wiremock.thirdparty.service.PaymentService;
import com.spring.wiremock.thirdparty.service.impl.FraudServiceImpl;
import com.spring.wiremock.thirdparty.service.impl.PaymentServiceImpl;
import com.spring.wiremock.util.Constants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.recordSpec;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookingPaymentServiceWithSingleRecordingTest {
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
        this.wireMockServer = new WireMockServer(8085);
        this.wireMockServer.start();

        this.wireMockServer.startRecording("http://localhost:8090");
        // this.wireMockServer.startRecording(config());

        PaymentService paymentService = new PaymentServiceImpl(thirdPartyProperties, restTemplate, jacksonMapper);
        FraudService fraudService = new FraudServiceImpl(thirdPartyProperties, restTemplate, jacksonMapper);
        this.bookingPaymentService = new BookingPaymentServiceImpl(paymentService, fraudService, thirdPartyProperties, jacksonMapper);
    }

    /***
     * Single third party call mocking using wire mock
     */
    @Order(1)
    @Test
    void updatePaymentTest() {
        String paymentId = "67e3084a-9bc1-4a92-be6b-1ddf403db8b1";
        BookingPaymentRequest request = BookingPaymentRequest.builder()
                .bookingId("1234")
                .amount(new BigDecimal("2500"))
                .cardDetails(CardDetails.builder()
                        .bank("UOB")
                        .expiry(LocalDate.of(2024, 10, 25))
                        .number("1111-6754-7865-7390")
                        .build())
                .build();

       PaymentRequest paymentRequest = PaymentRequest.builder()
                .cardNumber(request.getCardDetails().getNumber())
                .bank(request.getCardDetails().getBank())
                .cardExpiryDate(request.getCardDetails().getExpiry())
                .amount(request.getAmount())
                .build();

        StringBuilder updateUrlPath = new StringBuilder(Constants.FORWARD_SLASH)
                .append(this.thirdPartyProperties.getVersion())
                .append("/payments")
                .append(Constants.FORWARD_SLASH)
                .append(paymentId);

        // when
        BookingPaymentResponse response = this.bookingPaymentService.updatePayment(request, paymentId);

        //then
        assertThat(response.getBookingId()).isEqualTo(request.getBookingId());
        assertThat(response.getPaymentId()).isEqualTo(paymentId);
        assertThat(response.getStatus()).isEqualTo(PaymentStatusEnum.SUCCESS);

        // verify
       /* this.wireMockServer.verify(putRequestedFor(urlPathEqualTo(updateUrlPath.toString()))
                .withRequestBody(equalToJson(this.jacksonMapper.convertObjectToJson(paymentRequest))));*/
    }

    /***
     * Single third party call when card number is blacklisted mocking using wire mock
     */
    // @Order(2)
    // @Test
    void makePaymentTest() {

        BookingPaymentRequest request = BookingPaymentRequest.builder()
                .bookingId("1236")
                .amount(new BigDecimal("5000"))
                .cardDetails(CardDetails.builder()
                        .bank("UOB")
                        .expiry(LocalDate.of(2024, 10, 25))
                        .number("1234-3245-4325-1289")
                        .build())
                .build();

        FraudCheckRequest fraudCheckRequest = FraudCheckRequest.builder()
                .cardNumber(request.getCardDetails().getNumber())
                .bank(request.getCardDetails().getBank())
                .build();

        FraudCheckResponse expectedFraudCheckResponse = FraudCheckResponse.builder()
                .blacklisted(true)
                .build();

        //given
        StringBuilder fraudUrlPath = new StringBuilder(Constants.FORWARD_SLASH)
                .append(this.thirdPartyProperties.getVersion())
                .append("/fraudCheck");

        // Stubbing fraud check third party call
        this.wireMockServer.stubFor(post(urlPathEqualTo(fraudUrlPath.toString()))
                .withRequestBody(equalToJson(this.jacksonMapper.convertObjectToJson(fraudCheckRequest)))
                .willReturn(okJson(this.jacksonMapper.convertObjectToJson(expectedFraudCheckResponse))));

        // when
        BookingPaymentResponse response = this.bookingPaymentService.makePayment(request);

        //then
        assertThat(response.getBookingId()).isEqualTo(request.getBookingId());
        assertNull(response.getPaymentId());
        assertThat(response.getStatus()).isEqualTo(PaymentStatusEnum.REJECTED);

        // verify
        this.wireMockServer.verify(1, postRequestedFor(urlPathEqualTo(fraudUrlPath.toString()))
                .withRequestBody(equalToJson(this.jacksonMapper.convertObjectToJson(fraudCheckRequest))));
    }

    private RecordSpec config() {
        return recordSpec()
                .forTarget("http://localhost:8090")
                .makeStubsPersistent(true)
                .build();
    }

    @AfterEach
    void tearDown() {
        this.wireMockServer.stopRecording();
        this.wireMockServer.stop();
    }
}
