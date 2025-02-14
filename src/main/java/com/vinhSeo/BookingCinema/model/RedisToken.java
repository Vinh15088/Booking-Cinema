package com.vinhSeo.BookingCinema.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash("redis_token")
@Setter
@Getter
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RedisToken implements Serializable {
    String id;
    String accessToken;
    String refreshToken;
    String resetToken;
}
