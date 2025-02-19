package com.vinhSeo.BookingCinema.model;

import com.vinhSeo.BookingCinema.enums.CinemaStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cinema")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Cinema extends AbstractEntity<Integer> {

    @Column(name = "address", nullable = false)
    String address;

    @Column(name = "city", nullable = false)
    String city;

    @Column(name = "latitude", nullable = false, precision = 10, scale = 7)
    BigDecimal latitude;

    @Column(name = "longitude", nullable = false, precision = 10, scale = 7)
    BigDecimal longitude;

    @Column(name = "cinema_status")
    @Enumerated(EnumType.STRING)
    CinemaStatus cinemaStatus;

    @OneToMany(mappedBy = "cinema")
    Set<CinemaHall> cinemaHalls = new HashSet<>();

}
