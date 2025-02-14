package com.vinhSeo.BookingCinema.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RedisHash(value = "redis_mail_confirm", timeToLive = 300L)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RedisMailConfirm {
    String id;
    String email;
}
