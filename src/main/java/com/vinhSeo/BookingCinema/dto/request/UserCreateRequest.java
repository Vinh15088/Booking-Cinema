package com.vinhSeo.BookingCinema.dto.request;

import com.vinhSeo.BookingCinema.dto.validator.EnumValue;
import com.vinhSeo.BookingCinema.enums.Gender;
import com.vinhSeo.BookingCinema.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreateRequest implements Serializable {

    @NotBlank(message = "fullName must be not blank")
    String fullName;

    @EnumValue(name = "gender", enumClass = Gender.class)
    String gender;

    String dateOfBirth;

    String phone;

    @NotBlank(message = "email must be not blank")
    @Email(message = "email invalid")
    String email;

    @EnumValue(name = "userStatus", enumClass = UserStatus.class)
    String userStatus;

    @NotBlank(message = "username must be not blank")
    String username;

    @NotBlank(message = "password must be not blank")
    String password;
}
