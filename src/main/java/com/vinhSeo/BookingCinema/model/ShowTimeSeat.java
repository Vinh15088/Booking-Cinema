package com.vinhSeo.BookingCinema.model;

import com.vinhSeo.BookingCinema.enums.ShowTimeSeatStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "show_time_seat")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShowTimeSeat extends AbstractEntity<Integer> {

    @Enumerated(EnumType.STRING)
    @Column(name = "show_time_seat_stauts")
    ShowTimeSeatStatus showTimeSeatStatus;

    @ManyToOne
    @JoinColumn(name = "show_time_id")
    ShowTime showTime;

    @ManyToOne
    @JoinColumn(name = "seat_id")
    Seat seat;
}
