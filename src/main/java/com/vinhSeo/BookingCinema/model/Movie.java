package com.vinhSeo.BookingCinema.model;

import com.vinhSeo.BookingCinema.enums.MovieStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;
import java.util.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "movie")
public class Movie extends AbstractEntity<Integer> implements Serializable {

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

    @Column(name = "trailer")
    String trailer;

    @Column(name = "banner", nullable = false)
    String banner;

    @OneToMany(mappedBy = "movie")
    List<ShowTime> showTimes = new ArrayList<>();

    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    List<MovieHasMovieType> movieHasMovieTypes = new ArrayList<>();

    @OneToMany(mappedBy = "movie")
    List<Review> reviews = new ArrayList<>();
}
