package com.vinhSeo.BookingCinema.model;

import com.vinhSeo.BookingCinema.enums.RoomTypeStatus;
import jakarta.persistence.*;
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
@Table(name = "room_type")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomType extends AbstractEntity<Integer> {

    @Column(name = "name", unique = true, nullable = false)
    String name;

    @Column(name = "room_type_status")
    @Enumerated(EnumType.STRING)
    RoomTypeStatus roomTypeStatus;

    @OneToMany(mappedBy = "roomType")
    Set<CinemaHall> cinemaHalls = new HashSet<>();

}
