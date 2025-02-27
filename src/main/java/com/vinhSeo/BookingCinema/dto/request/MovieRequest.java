package com.vinhSeo.BookingCinema.dto.request;

import com.vinhSeo.BookingCinema.dto.validator.EnumValue;
import com.vinhSeo.BookingCinema.enums.MovieStatus;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieRequest implements Serializable {
    @NotBlank(message = "title must be not blank")
    String title;

    @NotBlank(message = "description must be not blank")
    String description;

    @Min(value = 1, message = "duration must be greater than 1")
    int duration;

    @NotBlank(message = "language must be not blank")
    String language;

    @NotNull(message = "age_limit must be not null")
    @Min(value = 1, message = "age_limit must greater than 1")
    int ageLimit;

    @NotBlank(message = "releaseDate must be not blank")
    String releaseDate; // type yyyy-MM-dd

    @EnumValue(name = "movie_status", enumClass = MovieStatus.class)
    String status;

    @NotBlank(message = "Url trailer must be  not blank")
    String trailer;

    @NotEmpty(message = "movieType must be not empty")
    List<Integer> movieType;
}
