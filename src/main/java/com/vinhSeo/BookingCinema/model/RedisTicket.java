package com.vinhSeo.BookingCinema.model;

import com.vinhSeo.BookingCinema.dto.request.TicketDetailRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@RedisHash(value = "redis_ticket", timeToLive = 310)
public class RedisTicket implements Serializable {

    @Id
    Integer userId;

    String transCode;
    Integer price;
    Integer showTimeId;
    Integer paymentStatus;
    List<TicketDetailRequest> ticketDetailRequests;
}
