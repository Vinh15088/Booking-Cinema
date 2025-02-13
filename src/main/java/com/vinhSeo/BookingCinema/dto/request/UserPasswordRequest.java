package com.vinhSeo.BookingCinema.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserPasswordRequest implements Serializable {

    @NotNull(message = "id must be not null")
    @Min(value = 1, message = "id must be equals or greater than 1")
    Integer id;

    @NotBlank(message = "oldPassword must be not blank")
    String oldPassword;

    @NotBlank(message = "password must be not blank")
    String password;

    @NotBlank(message = "confirmPassword must be not blank")
    String confirmPassword;
}
