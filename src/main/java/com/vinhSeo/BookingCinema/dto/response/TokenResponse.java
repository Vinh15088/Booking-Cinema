package com.vinhSeo.BookingCinema.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class TokenResponse implements Serializable {
    String accessToken;
    String refreshToken;
}
