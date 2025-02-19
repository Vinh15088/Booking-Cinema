package com.vinhSeo.BookingCinema.utils;

import com.vinhSeo.BookingCinema.enums.RoomTypeStatus;
import com.vinhSeo.BookingCinema.model.Role;
import com.vinhSeo.BookingCinema.model.RoomType;
import com.vinhSeo.BookingCinema.model.SeatType;
import com.vinhSeo.BookingCinema.repository.RoleRepository;
import com.vinhSeo.BookingCinema.repository.RoomTypeRepository;
import com.vinhSeo.BookingCinema.repository.SeatTypeRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "DATA_INITIALIZER")
@RequiredArgsConstructor
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final SeatTypeRepository seatTypeRepository;
    private final RoomTypeRepository roomTypeRepository;

    @PostConstruct
    public void initRole() {
        if(roleRepository.count() == 0) {
            roleRepository.save(Role.builder()
                    .name("ADMIN")
                    .description("Administrator role with full access")
                    .build());
            roleRepository.save(Role.builder()
                    .name("MANAGER")
                    .description("Manager role with limited administrative access")
                    .build());
            roleRepository.save(Role.builder()
                    .name("EMPLOYEE")
                    .description("Employee role with limited employee access")
                    .build());
            roleRepository.save(Role.builder()
                    .name("USER")
                    .description("Standard user role with basic access")
                    .build());
        }
    }

    @PostConstruct
    public void initSeatType() {
        if (seatTypeRepository.count() == 0) {
            seatTypeRepository.save(SeatType.builder()
                    .name("Standard")
                    .description("Standard seating with basic comfort")
                    .build());
            seatTypeRepository.save(SeatType.builder()
                    .name("VIP")
                    .description("VIP seating with extra comfort and amenities")
                    .build());
            seatTypeRepository.save(SeatType.builder()
                    .name("Couple")
                    .description("Couple seating designed for pairs, offering extra space and comfort")
                    .build());

            log.info("Sample seat types added to database.");
        }
    }

    @PostConstruct
    public void initRoomTypes() {
        if (roomTypeRepository.count() == 0) {
            roomTypeRepository.save(RoomType.builder()
                    .name("2D")
                    .roomTypeStatus(RoomTypeStatus.ACTIVE)
                    .build());

            roomTypeRepository.save(RoomType.builder()
                    .name("3D")
                    .roomTypeStatus(RoomTypeStatus.ACTIVE)
                    .build());

            log.info("Room type added to database.");
        }
    }

}
