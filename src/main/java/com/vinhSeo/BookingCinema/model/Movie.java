package com.vinhSeo.BookingCinema.model;

import com.vinhSeo.BookingCinema.enums.MovieStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "title", unique = true, nullable = false)
    String title;

    @Column(name = "description", length = 4088)
    String description;

    @Column(name = "duration", nullable = false)
    int duration;

    @Column(name = "language", length = 32)
    String language;

    @Column(name = "age_limit")
    int ageLimit;

    @Column(name = "release_date", nullable = false)
    @Temporal(TemporalType.DATE)
    Date releaseDate;

    @Column(name = "rating")
    Float rating;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    MovieStatus status;
}
