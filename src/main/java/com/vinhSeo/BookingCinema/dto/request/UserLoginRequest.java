package com.vinhSeo.BookingCinema.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserLoginRequest implements Serializable {
    String username;
    String password;
}
