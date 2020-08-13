package com.spring.wiremock.service;

import com.spring.wiremock.model.request.BatchPaymentRequest;
import com.spring.wiremock.model.request.BookingPaymentRequest;
import com.spring.wiremock.model.response.BookingPaymentResponse;

import java.util.List;

public interface BookingPaymentService {
    BookingPaymentResponse makePayment(BookingPaymentRequest bookingPaymentRequest);
    BookingPaymentResponse updatePayment(BookingPaymentRequest bookingPaymentRequest, String paymentId);
    List<BookingPaymentResponse> batchPayment(BatchPaymentRequest batchPaymentRequest);
}
