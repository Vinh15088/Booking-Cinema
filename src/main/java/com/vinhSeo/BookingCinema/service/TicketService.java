package com.vinhSeo.BookingCinema.service;

import com.vinhSeo.BookingCinema.dto.request.TicketDetailRequest;
import com.vinhSeo.BookingCinema.dto.request.TicketRequest;
import com.vinhSeo.BookingCinema.exception.AppException;
import com.vinhSeo.BookingCinema.exception.ErrorApp;
import com.vinhSeo.BookingCinema.mapper.TicketDetailMapper;
import com.vinhSeo.BookingCinema.mapper.TicketMapper;
import com.vinhSeo.BookingCinema.model.*;
import com.vinhSeo.BookingCinema.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j(topic = "TICKET_SERVICE")
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketDetailRepository ticketDetailRepository;
    private final TicketMapper ticketMapper;
    private final TicketDetailMapper ticketDetailMapper;
    private final TicketPriceRepository ticketPriceRepository;
    private final ShowTimeRepository showTimeRepository;
    private final ShowTimeSeatRepository showTimeSeatRepository;
    private final UserRepository userRepository;
    private final ShowTimeSeatService showTimeSeatService;
    private final RedisTicketRepository redisTicketRepository;

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String SHOW_TIME_SEAT_CACHE = "showtime_seat:";
    private static final Integer TTL = 10; // minutes

    public void setShowTimeSeatCache(Integer userId, Integer showTimeSeatId, String statusShowTimeSeat) {
        log.info("Push show time seat to cache");

        String key = SHOW_TIME_SEAT_CACHE + userId + ":" + showTimeSeatId;

        redisTemplate.opsForValue().set(key, statusShowTimeSeat, TTL, TimeUnit.MINUTES);
    }

    public String getShowTimeSeatCache(Integer userId, Integer showTimeSeatId) {
        log.info("Get show time seat from cache");

        String key = SHOW_TIME_SEAT_CACHE + userId + ":" + showTimeSeatId;

        return (String) redisTemplate.opsForValue().get(key);
    }

    public void deleteAllHeldSeatsByUser(Integer userId) {
        log.info("Delete show time seat from cache");

        String pattern = SHOW_TIME_SEAT_CACHE + userId + ":*";
        Set<String> keys = redisTemplate.keys(pattern);

        if(keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Ticket createTicket(Integer userId, TicketRequest ticketRequest) {
        log.info("Create ticket");

        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorApp.USER_NOT_FOUND));

        List<TicketDetailRequest> ticketDetailRequests = ticketRequest.getTicketDetailRequests();

        for(TicketDetailRequest request:ticketDetailRequests) {
            showTimeSeatService.changeStatus(request.getShowTimeSeatId(), "RESERVED");
        }

        Ticket ticket = ticketMapper.toTicket(ticketRequest, showTimeRepository,
                ticketDetailMapper, showTimeSeatRepository, ticketPriceRepository);
        ticket.setUser(user);

        Integer price = ticket.getTicketDetails().stream().mapToInt(detail -> detail.getPrice()).sum();
        ticket.setTotalAmount(price);

        ticketDetailRepository.saveAll(ticket.getTicketDetails());

        ticket.setBookingDate(new Date());

        if (ticket.getTicketDetails() != null) {
            ticket.getTicketDetails().forEach(detail -> detail.setTicket(ticket));
        }

        return ticketRepository.save(ticket);
    }

    public RedisTicket holdSeat(Integer userId, TicketRequest ticketRequest) {
        log.info("Hold seat");

        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorApp.USER_NOT_FOUND));

        List<TicketDetailRequest> ticketDetailRequests = ticketRequest.getTicketDetailRequests();

        deleteAllHeldSeatsByUser(userId);

        if (!isAvailable(userId, ticketDetailRequests)) {
            throw new AppException(ErrorApp.SHOW_TIME_SEAT_NOT_AVAILABLE);
        }


        // solve price
        Ticket ticket = ticketMapper.toTicket(ticketRequest, showTimeRepository,
                ticketDetailMapper, showTimeSeatRepository, ticketPriceRepository);
        ticket.setUser(user);

        Integer price = ticket.getTicketDetails().stream().mapToInt(detail -> detail.getPrice()).sum();

        // generate bookingId
        String bookingId = String.valueOf(ThreadLocalRandom.current().nextLong(1000000000L, 9999999999L));

        // push redisTicket to redis
        log.info("Save Redis Ticket with user id: {}", userId);
        RedisTicket redisTicket = RedisTicket.builder()
                .userId(userId)
                .bookingId(bookingId)
                .price(price)
                .ticketDetailRequests(ticketDetailRequests)
                .showTimeId(ticketRequest.getShowTime())
                .build();

        return redisTicketRepository.save(redisTicket);
    }

    public boolean isAvailable(Integer userId, List<TicketDetailRequest> ticketDetailRequests) {

        List<ShowTimeSeat> showTimeSeats = new ArrayList<>();

        for(TicketDetailRequest request:ticketDetailRequests) {
            ShowTimeSeat showTimeSeat = showTimeSeatRepository.findById(request.getShowTimeSeatId()).orElseThrow(()
                    -> new AppException(ErrorApp.SHOW_TIME_SEAT_NOT_FOUND));

            // check showTimeSeat in redis
            String status = getShowTimeSeatCache(userId, showTimeSeat.getId());
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
                setShowTimeSeatCache(userId, showTimeSeat.getId(), "PENDING");
            }

            return true;
        }

        return false;
    }

    public void cancelHoldSeat(Integer userId) {
        log.info("Cancel hold seat");

        RedisTicket redisTicket = redisTicketRepository.findById(userId).orElseThrow(()
                -> new AppException(ErrorApp.REDIS_TICKET_NOT_FOUND));

        List<TicketDetailRequest> ticketDetailRequests = redisTicket.getTicketDetailRequests();

        // delete all previous showTimeSeat of user
        for(TicketDetailRequest request:ticketDetailRequests) {
            String key = SHOW_TIME_SEAT_CACHE + userId + ":" + request.getShowTimeSeatId();
            redisTemplate.delete(key);
        }

        redisTicketRepository.delete(redisTicket);
    }

    public Ticket getById(Integer id) {
        log.info("Get ticket by id");

        return ticketRepository.findById(id).orElseThrow(() -> new AppException(ErrorApp.TICKET_NOT_FOUND));
    }

    public List<Ticket> getAllTickets(Integer userId, Integer showTimeId) {
        log.info("Get all tickets or by userId={}, showTimeId={}", userId, showTimeId);

        if (userId != null) {
            log.info("Fetching tickets by userId={}", userId);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new AppException(ErrorApp.USER_NOT_FOUND));
            return ticketRepository.findAllByUser(user);
        }

        if (showTimeId != null) {
            log.info("Fetching tickets by showTimeId={}", showTimeId);
            ShowTime showTime = showTimeRepository.findById(showTimeId)
                    .orElseThrow(() -> new AppException(ErrorApp.SHOW_TIME_NOT_FOUND));
            return ticketRepository.findAllByShowTime(showTime);
        }

        log.info("Fetching all tickets");
        return ticketRepository.findAll();
    }

}
