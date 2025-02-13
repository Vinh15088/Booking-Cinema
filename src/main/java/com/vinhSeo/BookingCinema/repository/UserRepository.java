package com.vinhSeo.BookingCinema.repository;

import com.vinhSeo.BookingCinema.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    User findByEmail(String email);

    @Query("SELECT u from User u WHERE " +
            "LOWER(u.username) LIKE %:keyword% " +
            "OR LOWER(u.email) LIKE %:keyword% " +
            "OR LOWER(u.fullName) LIKE %:keyword% " +
            "OR LOWER(u.phone) LIKE %:keyword%")
    Page<User> searchUser(String keyword, Pageable pageable);
}
