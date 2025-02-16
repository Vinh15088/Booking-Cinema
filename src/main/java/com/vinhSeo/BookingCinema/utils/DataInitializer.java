package com.vinhSeo.BookingCinema.utils;

import com.vinhSeo.BookingCinema.enums.RoomTypeStatus;
import com.vinhSeo.BookingCinema.model.RoomType;
import com.vinhSeo.BookingCinema.model.SeatType;
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

    private final SeatTypeRepository seatTypeRepository;
    private final RoomTypeRepository roomTypeRepository;

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
                    .name("Premium")
                    .description("Premium seating with the best view and comfort")
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
