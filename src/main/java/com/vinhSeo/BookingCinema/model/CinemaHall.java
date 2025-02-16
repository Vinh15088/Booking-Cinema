package com.vinhSeo.BookingCinema.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cinema_hall")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CinemaHall extends AbstractEntity<Integer> {

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "total_seats")
    Integer totalSeats;

    @ManyToOne
    @JoinColumn(name = "cinema_id")
    Cinema cinema;

    @ManyToOne
    @JoinColumn(name = "room_type_id")
    RoomType roomType;

    @OneToMany(mappedBy = "cinemaHall")
    List<Seat> seats = new ArrayList<>();

}
