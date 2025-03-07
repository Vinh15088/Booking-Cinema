package com.vinhSeo.BookingCinema.model;

import com.vinhSeo.BookingCinema.enums.TokenType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "black_list_token")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlackListToken {

    @Id
    @Column(name = "id", nullable = false)
    String id;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    Date createdAt;

    @Column(name = "token", nullable = false, length = 512)
    String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_type", nullable = false)
    TokenType tokenType;

    @Column(name = "expired_at", nullable = false)
    Date expiredAt;
}
