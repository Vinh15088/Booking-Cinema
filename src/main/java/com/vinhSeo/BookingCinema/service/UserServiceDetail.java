package com.vinhSeo.BookingCinema.service;

import com.vinhSeo.BookingCinema.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceDetail {

    private final UserRepository userRepository;

    public UserDetailsService userDetailsService() {
        return userRepository::findByUsername;
    }
}
