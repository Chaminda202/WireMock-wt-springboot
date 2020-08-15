package com.spring.wiremock.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.spring.wiremock.config.JacksonMapper;
import com.spring.wiremock.config.ThirdPartyProperties;
import com.spring.wiremock.enumeration.PaymentStatusEnum;
import com.spring.wiremock.model.request.BatchPaymentRequest;
import com.spring.wiremock.model.request.BookingPaymentRequest;
import com.spring.wiremock.model.request.CardDetails;
import com.spring.wiremock.model.response.BookingPaymentResponse;
import com.spring.wiremock.service.impl.BookingPaymentServiceImpl;
import com.spring.wiremock.thirdparty.model.response.FraudCheckResponse;
import com.spring.wiremock.thirdparty.model.response.PaymentResponse;
import com.spring.wiremock.thirdparty.service.FraudService;
import com.spring.wiremock.thirdparty.service.PaymentService;
import com.spring.wiremock.thirdparty.service.impl.FraudServiceImpl;
import com.spring.wiremock.thirdparty.service.impl.PaymentServiceImpl;
import com.spring.wiremock.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/***
 * Payload is Nondeterministic
 */
@SpringBootTest
public class BookingPaymentServiceWhenContentIsNotFixStubbingTest {
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
        FraudService fraudService = new FraudServiceImpl(thirdPartyProperties, restTemplate, jacksonMapper);
        this.bookingPaymentService = new BookingPaymentServiceImpl(paymentService, fraudService, thirdPartyProperties, jacksonMapper);
    }

    @Test
    void batchPaymentTest() {
        //given
        StringBuilder fraudUrlPath = new StringBuilder(Constants.FORWARD_SLASH)
                .append(this.thirdPartyProperties.getVersion())
                .append("/fraudCheck");

        StringBuilder paymentUrlPath = new StringBuilder(Constants.FORWARD_SLASH)
                .append(this.thirdPartyProperties.getVersion())
                .append("/payments");

        PaymentResponse expectedPaymentResponse = PaymentResponse.builder()
                .paymentId(UUID.randomUUID().toString())
                .status(PaymentStatusEnum.SUCCESS)
                .build();

        FraudCheckResponse expectedFraudCheckResponse = FraudCheckResponse.builder()
                .blacklisted(false)
                .build();

        // Stubbing fraud check third party call
        this.wireMockServer.stubFor(post(urlPathEqualTo(fraudUrlPath.toString()))
                .withRequestBody(
                        matchingJsonPath("bank")
                )
                .withRequestBody(
                        matchingJsonPath("cardNumber")
                )
                .willReturn(
                        okJson(this.jacksonMapper.convertObjectToJson(expectedFraudCheckResponse))
                )
        );

        // Stubbing make payment third party call
        this.wireMockServer.stubFor(post(urlPathEqualTo(paymentUrlPath.toString()))
                .withRequestBody(
                        matchingJsonPath("bank")
                )
                .withRequestBody(
                        matchingJsonPath("cardNumber")
                )
                .withRequestBody(
                        matchingJsonPath("cardExpiryDate")
                )
                .withRequestBody(
                        matchingJsonPath("amount")
                )
                .willReturn(
                        okJson(this.jacksonMapper.convertObjectToJson(expectedPaymentResponse))
                )
        );
        List<BookingPaymentRequest> bookingPaymentRequestList = IntStream.range(0, 5)
                .mapToObj(this::generateBookingPaymentRequest)
                .collect(Collectors.toList());

        BatchPaymentRequest request = BatchPaymentRequest.builder()
                .paymentRequests(bookingPaymentRequestList)
                .build();

        // when
        List<BookingPaymentResponse> responses = this.bookingPaymentService.batchPayment(request);

        // then
        responses.forEach(response -> {
            assertNotNull(response.getPaymentId());
            assertNotNull(response.getBookingId());
            assertThat(response.getStatus()).isEqualTo(PaymentStatusEnum.SUCCESS);
        });
    }

    private BookingPaymentRequest generateBookingPaymentRequest(int i) {
        CardDetails cardDetails = CardDetails.builder()
                .expiry(LocalDate.of(2025, 01, 10).plusDays((long)i))
                .bank("DBS")
                .number(cardNumberGenerator())
                .build();

        BookingPaymentRequest request = BookingPaymentRequest.builder()
                .amount(new BigDecimal(randomFourNumbersString()))
                .bookingId(UUID.randomUUID().toString())
                .cardDetails(cardDetails)
                .build();
        return request;
    }

    private String cardNumberGenerator() {
        StringBuilder cardNumber = new StringBuilder()
                .append(randomFourNumbersString())
                .append("-")
                .append(randomFourNumbersString())
                .append("-")
                .append(randomFourNumbersString())
                .append("-")
                .append(randomFourNumbersString());
        return cardNumber.toString();
    }

    private String randomFourNumbersString() {
        return new DecimalFormat("0000").format(new Random().nextInt(9999));
    }
}
