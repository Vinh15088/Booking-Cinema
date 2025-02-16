package com.vinhSeo.BookingCinema.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "seat_type")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SeatType extends AbstractEntity<Integer> {

    @Column(name = "name", nullable = false, unique = true)
    String name;

    @Column(name = "description")
    String description;

    @OneToMany(mappedBy = "seatType")
    Set<Seat> seats = new HashSet<>();
}
