package com.vinhSeo.BookingCinema.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    @Column(name = "hall_row", nullable = false)
    Integer hallRow;

    @Column(name = "hall_column", nullable = false)
    Integer hallColumn;

    @Column(name = "available", nullable = false)
    Boolean available;

    @ManyToOne
    @JoinColumn(name = "cinema_id")
    Cinema cinema;

    @ManyToOne
    @JoinColumn(name = "room_type_id")
    RoomType roomType;

    @OneToMany(mappedBy = "cinemaHall")
    @OnDelete(action = OnDeleteAction.CASCADE)
    List<Seat> seats = new ArrayList<>();

    @OneToMany(mappedBy = "cinemaHall")
    List<ShowTime> showTimes = new ArrayList<>();

}
