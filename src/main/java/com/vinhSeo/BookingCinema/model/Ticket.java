package com.vinhSeo.BookingCinema.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;
import java.util.List;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ticket")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Ticket extends AbstractEntity<Integer> {

    @Column(name = "total_amount", nullable = false)
    Integer totalAmount;

    @Column(name = "booking_date", nullable = false)
    Date bookingDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name = "show_time_id")
    ShowTime showTime;

    @OneToMany(mappedBy = "ticket")
    @OnDelete(action = OnDeleteAction.CASCADE)
    List<TicketDetail> ticketDetails;

    @OneToOne(mappedBy = "ticket")
    Payment payment;

}
