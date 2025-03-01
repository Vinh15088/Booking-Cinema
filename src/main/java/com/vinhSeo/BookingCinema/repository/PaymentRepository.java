package com.vinhSeo.BookingCinema.repository;

import com.vinhSeo.BookingCinema.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

}
