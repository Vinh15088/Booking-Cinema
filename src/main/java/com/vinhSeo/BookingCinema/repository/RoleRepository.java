package com.vinhSeo.BookingCinema.repository;

import com.vinhSeo.BookingCinema.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByName(String name);
}
