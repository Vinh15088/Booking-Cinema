package com.vinhSeo.BookingCinema.dto.request;

import com.vinhSeo.BookingCinema.dto.validator.EnumValue;
import com.vinhSeo.BookingCinema.enums.Gender;
import com.vinhSeo.BookingCinema.enums.UserStatus;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest implements Serializable {

    String fullName;

    @EnumValue(name = "gender", enumClass = Gender.class)
    String gender;

    String dateOfBirth;

    String phone;

    @Email(message = "Email invalid")
    String email;

    @EnumValue(name = "userStatus", enumClass = UserStatus.class)
    String userStatus;
}
