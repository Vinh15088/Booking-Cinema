package com.vinhSeo.BookingCinema.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserPasswordRequest implements Serializable {

    @NotBlank(message = "oldPassword must be not blank")
    String oldPassword;

    @NotBlank(message = "password must be not blank")
    String password;

    @NotBlank(message = "confirmPassword must be not blank")
    String confirmPassword;
}
