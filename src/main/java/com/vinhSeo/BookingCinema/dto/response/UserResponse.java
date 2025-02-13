package com.vinhSeo.BookingCinema.dto.response;

import com.vinhSeo.BookingCinema.enums.Gender;
import com.vinhSeo.BookingCinema.enums.UserStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse implements Serializable {
    Integer id;
    String fullName;
    Gender gender;
    Date dateOfBirth;
    String phone;
    String email;
    UserStatus userStatus;
    Date createAt;
    Date updatedAt;
    String username;
    String password;
}
