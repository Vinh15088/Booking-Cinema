package com.vinhSeo.BookingCinema.mapper;

import com.vinhSeo.BookingCinema.dto.request.CinemaRequest;
import com.vinhSeo.BookingCinema.dto.response.CinemaResponse;
import com.vinhSeo.BookingCinema.model.Cinema;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CinemaMapper {

    @Mapping(target = "cinemaStatus", expression = "java(parseEnum(request.getCinemaStatus(), CinemaStatus.class))")
    Cinema toCinema(CinemaRequest request);

    CinemaResponse toCinemaResponse(Cinema cinema);

    default <E extends Enum<E>> E parseEnum(String value, Class<E> enumType) {
        try {
            return Enum.valueOf(enumType, value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid value for " + enumType.getSimpleName() + ": " + value);
        }
    }
}
