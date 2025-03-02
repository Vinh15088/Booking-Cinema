package com.vinhSeo.BookingCinema.service;

import com.vinhSeo.BookingCinema.dto.request.TicketDetailRequest;
import com.vinhSeo.BookingCinema.dto.request.TicketRequest;
import com.vinhSeo.BookingCinema.exception.AppException;
import com.vinhSeo.BookingCinema.exception.ErrorApp;
import com.vinhSeo.BookingCinema.mapper.TicketDetailMapper;
import com.vinhSeo.BookingCinema.mapper.TicketMapper;
import com.vinhSeo.BookingCinema.model.RedisTicket;
import com.vinhSeo.BookingCinema.model.ShowTimeSeat;
import com.vinhSeo.BookingCinema.model.Ticket;
import com.vinhSeo.BookingCinema.model.User;
import com.vinhSeo.BookingCinema.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j(topic = "BOOKING_SERVICE")
@RequiredArgsConstructor
public class BookingService {

    private final TicketMapper ticketMapper;
    private final TicketDetailMapper ticketDetailMapper;
    private final TicketPriceRepository ticketPriceRepository;
    private final ShowTimeRepository showTimeRepository;
    private final ShowTimeSeatRepository showTimeSeatRepository;
    private final UserRepository userRepository;
    private final RedisTicketRepository redisTicketRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisShowTimeSeatService redisShowTimeSeatService;

    private String getCurrentTimeString(String format) {
        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setCalendar(calendar);

        return sdf.format(calendar.getTimeInMillis());
    }

    private String getRandomNumber(Integer length) {
        Random random = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(length);

        for(int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }

    public RedisTicket bookingTicket(Integer userId, TicketRequest ticketRequest) {
        log.info("Hold seat");

        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorApp.USER_NOT_FOUND));

        List<TicketDetailRequest> ticketDetailRequests = ticketRequest.getTicketDetailRequests();

        redisShowTimeSeatService.deleteAllHeldSeatsByUser(userId);

        // check all show time seat in ticket request
        if (!isAvailable(userId, ticketDetailRequests)) {
            throw new AppException(ErrorApp.SHOW_TIME_SEAT_NOT_AVAILABLE);
        }

        // solve price
        Ticket ticket = ticketMapper.toTicket(ticketRequest, showTimeRepository,
                ticketDetailMapper, showTimeSeatRepository, ticketPriceRepository);
        ticket.setUser(user);

        Integer price = ticket.getTicketDetails().stream().mapToInt(detail -> detail.getPrice()).sum();

        // generate transCode
        String transCode = getCurrentTimeString("yyMMdd") + "_" + getRandomNumber(10);

        // push redisTicket to redis
        log.info("Save Redis Ticket with user id: {}", userId);
        RedisTicket redisTicket = RedisTicket.builder()
                .userId(userId)
                .transCode(transCode)
                .price(price)
                .paymentStatus(0)
                .ticketDetailRequests(ticketDetailRequests)
                .showTimeId(ticketRequest.getShowTime())
                .build();

        return redisTicketRepository.save(redisTicket);
    }

    // check list ticket detail
    public boolean isAvailable(Integer userId, List<TicketDetailRequest> ticketDetailRequests) {

        List<ShowTimeSeat> showTimeSeats = new ArrayList<>();

        for(TicketDetailRequest request:ticketDetailRequests) {
            ShowTimeSeat showTimeSeat = showTimeSeatRepository.findById(request.getShowTimeSeatId()).orElseThrow(()
                    -> new AppException(ErrorApp.SHOW_TIME_SEAT_NOT_FOUND));

            // check showTimeSeat in redis
            String status = redisShowTimeSeatService.getShowTimeSeatCache(userId, showTimeSeat.getId());
            if (status != null && status.equals("PENDING")) {
                log.info("Seat {} is already held by another user", request.getShowTimeSeatId());
                return false;
            }

            log.info("Show time seat status: {}", showTimeSeat.getShowTimeSeatStatus().toString());

            if(showTimeSeat.getShowTimeSeatStatus().toString().equals("AVAILABLE")) {
                showTimeSeats.add(showTimeSeat);
            }

        }

        // check all show
        if(ticketDetailRequests.size() == showTimeSeats.size()) {
            for(ShowTimeSeat showTimeSeat:showTimeSeats) {
                redisShowTimeSeatService.setShowTimeSeatCache(userId, showTimeSeat.getId(), "PENDING");
            }

            return true;
        }

        return false;
    }

    public void cancelBooking(Integer userId) {
        log.info("Cancel booking seat");

        RedisTicket redisTicket = redisTicketRepository.findById(userId).orElseThrow(()
                -> new AppException(ErrorApp.REDIS_TICKET_NOT_FOUND));

        List<TicketDetailRequest> ticketDetailRequests = redisTicket.getTicketDetailRequests();

        // delete all previous showTimeSeat of user
        for(TicketDetailRequest request:ticketDetailRequests) {
            String key = "showtime_seat:" + userId + ":" + request.getShowTimeSeatId();
            redisTemplate.delete(key);
        }

        redisTicketRepository.delete(redisTicket);
    }

}


