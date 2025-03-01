package com.vinhSeo.BookingCinema.service;

import com.vinhSeo.BookingCinema.model.Payment;
import com.vinhSeo.BookingCinema.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "PAYMENT_SERVICE")
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public Payment createPayment(Payment payment) {
        log.info("Payment create");

        return paymentRepository.save(payment);
    }
}
