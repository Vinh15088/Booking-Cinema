package com.vinhSeo.BookingCinema.repository;

import com.vinhSeo.BookingCinema.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Integer> {
}
