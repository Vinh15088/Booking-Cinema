package com.vinhSeo.BookingCinema.mapper;

import com.vinhSeo.BookingCinema.dto.request.UserCreateRequest;
import com.vinhSeo.BookingCinema.dto.request.UserUpdateRequest;
import com.vinhSeo.BookingCinema.dto.response.UserResponse;
import com.vinhSeo.BookingCinema.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "dateOfBirth", expression = "java(parseDate(request.getDateOfBirth()))")
    @Mapping(target = "gender", expression = "java(parseEnum(request.getGender(), Gender.class))")
    @Mapping(target = "userStatus", expression = "java(parseEnum(request.getUserStatus(), UserStatus.class))")
    User toUserCreate(UserCreateRequest request);

    @Mapping(target = "dateOfBirth", expression = "java(parseDate(request.getDateOfBirth()))")
    @Mapping(target = "gender", expression = "java(parseEnum(request.getGender(), Gender.class))")
    @Mapping(target = "userStatus", expression = "java(parseEnum(request.getUserStatus(), UserStatus.class))")
    @Mapping(target = "password", ignore = true)
    User toUserUpdate(UserUpdateRequest request);

    UserResponse toUserResponse(User user);

    default Date parseDate(String dateOfBirth) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.parse(dateOfBirth);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please use yyyy-MM-dd.");
        }
    }

    default <E extends Enum<E>> E parseEnum(String value, Class<E> enumType) {
        try {
            return Enum.valueOf(enumType, value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid value for " + enumType.getSimpleName() + ": " + value);
        }
    }
}
