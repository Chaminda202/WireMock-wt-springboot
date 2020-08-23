package com.spring.wiremock.service.impl;

import com.spring.wiremock.config.JacksonMapper;
import com.spring.wiremock.config.ThirdPartyProperties;
import com.spring.wiremock.enumeration.PaymentStatusEnum;
import com.spring.wiremock.model.request.BatchPaymentRequest;
import com.spring.wiremock.model.request.BookingPaymentRequest;
import com.spring.wiremock.model.response.BookingPaymentResponse;
import com.spring.wiremock.service.BookingPaymentService;
import com.spring.wiremock.thirdparty.model.request.FraudCheckRequest;
import com.spring.wiremock.thirdparty.model.request.PaymentRequest;
import com.spring.wiremock.thirdparty.model.response.FraudCheckResponse;
import com.spring.wiremock.thirdparty.model.response.PaymentResponse;
import com.spring.wiremock.thirdparty.service.FraudService;
import com.spring.wiremock.thirdparty.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class BookingPaymentServiceImpl implements BookingPaymentService {
    private static final Logger LOG = LoggerFactory.getLogger(BookingPaymentServiceImpl.class);
    private final PaymentService paymentService;
    private final FraudService fraudService;
    private final ThirdPartyProperties thirdPartyProperties;
    private final JacksonMapper jacksonMapper;

    @Override
    public BookingPaymentResponse makePayment(BookingPaymentRequest bookingPaymentRequest) {
        LOG.info("Start to  call make payment in booking service {}", this.jacksonMapper.convertObjectToJson(bookingPaymentRequest));
        if(this.thirdPartyProperties.getTraceHolder().compareTo(bookingPaymentRequest.getAmount()) < 0) {
            LOG.info("Payment amount is greater than define trace holder limit");
            FraudCheckRequest fraudCheckRequest = FraudCheckRequest.builder()
                    .bank(bookingPaymentRequest.getCardDetails().getBank())
                    .cardNumber(bookingPaymentRequest.getCardDetails().getNumber())
                    .build();
            FraudCheckResponse fraudCheckResponse = this.fraudService.checkFraudStatus(fraudCheckRequest);

            if(fraudCheckResponse.isBlacklisted()) {
                LOG.info("Card number is back listed");
                return BookingPaymentResponse.builder()
                        .bookingId(bookingPaymentRequest.getBookingId())
                        .status(PaymentStatusEnum.REJECTED)
                        .build();
            }
        }

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .cardNumber(bookingPaymentRequest.getCardDetails().getNumber())
                .bank(bookingPaymentRequest.getCardDetails().getBank())
                .cardExpiryDate(bookingPaymentRequest.getCardDetails().getExpiry())
                .amount(bookingPaymentRequest.getAmount())
                .build();
        PaymentResponse paymentResponse = this.paymentService.makePayment(paymentRequest);
        LOG.info("End to call make payment in booking service");
        return BookingPaymentResponse.builder()
                .bookingId(bookingPaymentRequest.getBookingId())
                .paymentId(paymentResponse.getPaymentId())
                .status(paymentResponse.getStatus())
                .build();
    }

    @Override
    public BookingPaymentResponse updatePayment(BookingPaymentRequest bookingPaymentRequest, String paymentId) {
        LOG.info("Start to  call update payment in booking service payload - {} payment id - {}",
                this.jacksonMapper.convertObjectToJson(bookingPaymentRequest),  paymentId);
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .paymentId(paymentId)
                .cardNumber(bookingPaymentRequest.getCardDetails().getNumber())
                .bank(bookingPaymentRequest.getCardDetails().getBank())
                .cardExpiryDate(bookingPaymentRequest.getCardDetails().getExpiry())
                .amount(bookingPaymentRequest.getAmount())
                .build();
        PaymentResponse paymentResponse = this.paymentService.updatePayment(paymentRequest);
        LOG.info("End to call update payment in booking service");
        return BookingPaymentResponse.builder()
                .bookingId(bookingPaymentRequest.getBookingId())
                .paymentId(paymentResponse.getPaymentId())
                .status(paymentResponse.getStatus())
                .build();
    }

    @Override
    public List<BookingPaymentResponse> batchPayment(BatchPaymentRequest batchPaymentRequest) {
        LOG.info("Call batch payment in booking service");
        return batchPaymentRequest.getPaymentRequests()
                .stream()
                .map(this::makePayment)
                .collect(toList());
    }
}
