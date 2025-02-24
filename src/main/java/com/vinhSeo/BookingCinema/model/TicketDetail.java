package com.vinhSeo.BookingCinema.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ticket_detail")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TicketDetail extends AbstractEntity<Integer> {

    @Column(name = "price", nullable = false)
    Integer price;

    @Column(name = "show_time_seat_id", nullable = false)
    Integer showTimeSeatId;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    Ticket ticket;
}
