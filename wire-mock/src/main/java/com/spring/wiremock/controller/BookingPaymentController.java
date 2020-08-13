package com.spring.wiremock.controller;

import com.spring.wiremock.model.request.BatchPaymentRequest;
import com.spring.wiremock.model.request.BookingPaymentRequest;
import com.spring.wiremock.model.response.BookingPaymentResponse;
import com.spring.wiremock.service.BookingPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(value = "bookingPayments")
@RequiredArgsConstructor
public class BookingPaymentController {
    private final BookingPaymentService bookingPaymentService;

    @PostMapping(value = "single")
    public ResponseEntity<BookingPaymentResponse> makePayment(@Valid @RequestBody BookingPaymentRequest request) {
        BookingPaymentResponse response = this.bookingPaymentService.makePayment(request);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping(value = "/{paymentId}")
    public ResponseEntity<BookingPaymentResponse> updatePayment(@PathVariable(value = "paymentId") @NotNull String paymentId, @Valid @RequestBody BookingPaymentRequest request) {
        BookingPaymentResponse response = this.bookingPaymentService.updatePayment(request, paymentId);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping(value = "multiple")
    public ResponseEntity<List<BookingPaymentResponse>> batchPayment(@Valid @RequestBody BatchPaymentRequest request) {
        List<BookingPaymentResponse> response = this.bookingPaymentService.batchPayment(request);
        return ResponseEntity.ok().body(response);
    }
}
