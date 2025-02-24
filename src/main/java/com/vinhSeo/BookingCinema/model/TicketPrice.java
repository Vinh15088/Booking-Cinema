package com.vinhSeo.BookingCinema.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ticket_price")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TicketPrice extends AbstractEntity<Integer> {

    @Column(name = "price", nullable = false)
    Integer price;

    @Column(name = "room_type_id", nullable = false)
    Integer roomType;

    @Column(name = "seat_type_id", nullable = false)
    Integer seatType;

}
