package com.vinhSeo.BookingCinema.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "show_time")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShowTime extends AbstractEntity<Integer> {

    @Column(name = "start_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    Date startTime;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Column(name = "end_time", nullable = false)
    Date endTime;

    @Column(name = "price", nullable = false)
    Integer price;

    @Column(name = "status", nullable = false)
    Boolean status;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    Movie movie;

    @ManyToOne
    @JoinColumn(name = "cinema_hall_id")
    CinemaHall cinemaHall;

//    @OneToMany(mappedBy = "showTime")
//    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(mappedBy = "showTime", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<ShowTimeSeat> showTimeSeats;

    @OneToMany(mappedBy = "showTime")
    List<Ticket> tickets = new ArrayList<>();

}
