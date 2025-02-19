package com.vinhSeo.BookingCinema.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShowTimeRequest implements Serializable {

    @NotNull(message = "startTime must be not null")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    Date startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    Date endTime;

    @NotNull(message = "price must be not null")
    Integer price;

    @NotNull(message = "status must be not null")
    Boolean status;

    @NotNull(message = "movieId must be not null")
    @Min(value = 1, message = "movieId is greater than 0")
    Integer movieId;

    @NotNull(message = "cinemaHallId must be not null")
    @Min(value = 1, message = "cinemaHallId is greater than 0")
    Integer cinemaHallId;
}
