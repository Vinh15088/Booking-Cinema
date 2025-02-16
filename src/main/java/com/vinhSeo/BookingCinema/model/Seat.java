package com.vinhSeo.BookingCinema.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "seat")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Seat extends AbstractEntity<Integer> {

    @Column(name = "seat_number", nullable = false)
    String seatNumber;

    @Column(name = "seat_status")
    Boolean seatStatus;

    @ManyToOne
    @JoinColumn(name = "seat_type_id")
    SeatType seatType;

    @ManyToOne
    @JoinColumn(name = "cinama_hall_id")
    CinemaHall cinemaHall;
}
