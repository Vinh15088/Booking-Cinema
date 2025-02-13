package com.vinhSeo.BookingCinema.model;

import com.vinhSeo.BookingCinema.enums.Gender;
import com.vinhSeo.BookingCinema.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "user")
public class User extends AbstractEntity<Integer> implements UserDetails, Serializable {
    @Column(name = "full_name", nullable = false)
    String fullName;

    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    Gender gender;

    @Column(name = "date_of_birth")
    Date dateOfBirth;

    @Column(name = "phone")
    String phone;

    @Column(name = "email", nullable = false, unique = true)
    String email;

    @Column(name = "user_status")
    @Enumerated(EnumType.STRING)
    UserStatus userStatus;

    @Column(name = "username", unique = true)
    String username;

    @Column(name = "password")
    String password;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    Set<UserHasRole> roles = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<Role> roleList = roles.stream().map(UserHasRole::getRole).toList();

        List<String> roleNames = roleList.stream().map(Role::getName).toList();

        return roleNames.stream().map(SimpleGrantedAuthority::new).toList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserStatus.ACTIVE.equals(userStatus);
    }
}
