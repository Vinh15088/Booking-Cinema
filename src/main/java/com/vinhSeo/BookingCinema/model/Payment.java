package com.vinhSeo.BookingCinema.model;

import com.vinhSeo.BookingCinema.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "payment")
public class Payment extends AbstractEntity<Integer> {

    @Column(name = "price", nullable = false)
    Integer price;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    PaymentMethod paymentMethod;

    @Column(name = "transaction_id", nullable = false)
    String transactionId;

    @Column(name = "status", nullable = false)
    Boolean status;

    @Column(name = "payment_date", nullable = false)
    Date paymentDate;

    @OneToOne
    @JoinColumn(name = "ticket_id")
    Ticket ticket;

}
