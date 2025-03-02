package com.vinhSeo.BookingCinema.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.vinhSeo.BookingCinema.dto.request.TicketDetailRequest;
import com.vinhSeo.BookingCinema.dto.request.TicketRequest;
import com.vinhSeo.BookingCinema.enums.PaymentMethod;
import com.vinhSeo.BookingCinema.exception.AppException;
import com.vinhSeo.BookingCinema.exception.ErrorApp;
import com.vinhSeo.BookingCinema.mapper.TicketDetailMapper;
import com.vinhSeo.BookingCinema.mapper.TicketMapper;
import com.vinhSeo.BookingCinema.model.*;
import com.vinhSeo.BookingCinema.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    private final PaymentService paymentService;
    private final RedisShowTimeSeatService redisShowTimeSeatService;
    private final RedisTicketRepository redisTicketRepository;


    @Transactional(rollbackFor = Exception.class)
    @KafkaListener(topics = "PAYMENT_SUCCESS_TOPIC", groupId = "PAYMENT_SUCCESS_GROUP")
    public Ticket createTicket(JsonNode message) {
        log.info("Create ticket");

        // get info in kafka message
        Integer userId = message.get("userId").asInt();
        String transitionId = message.get("transitionId").asText();
        Integer price = message.get("price").asInt();
        Integer showTimeId = message.get("showTimeId").asInt();

        List<TicketDetailRequest> ticketDetailRequests = new ArrayList<>();

        ArrayNode arrayNode = (ArrayNode) message.get("ticketDetailRequests");

        for(JsonNode detailRequest : arrayNode) {
            TicketDetailRequest request = new TicketDetailRequest();
            request.setShowTimeSeatId(detailRequest.asInt());

            ticketDetailRequests.add(request);
        }

        TicketRequest ticketRequest = TicketRequest.builder()
                .showTime(showTimeId)
                .ticketDetailRequests(ticketDetailRequests)
                .build();

        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorApp.USER_NOT_FOUND));

        // change status RESERVED in db -> booking successfully
        for(TicketDetailRequest request:ticketDetailRequests) {
            showTimeSeatService.changeStatus(request.getShowTimeSeatId(), "RESERVED");
        }

        Ticket ticket = ticketMapper.toTicket(ticketRequest, showTimeRepository,
                ticketDetailMapper, showTimeSeatRepository, ticketPriceRepository);
        ticket.setUser(user);
        ticket.setTotalAmount(price);
        ticket.setBookingDate(new Date());

        // save ticket detail in db
        ticketDetailRepository.saveAll(ticket.getTicketDetails());

        if (ticket.getTicketDetails() != null) {
            ticket.getTicketDetails().forEach(detail -> detail.setTicket(ticket));
        }

        // save payment
        Payment payment = Payment.builder()
                .ticket(ticket)
                .price(price)
                .status(true)
                .transactionId(transitionId)
                .paymentDate(new Date())
                .paymentMethod(PaymentMethod.ZALOPAY)
                .build();

        paymentService.createPayment(payment);

        // delete show time seat of user in redis after booking successfully
        redisShowTimeSeatService.deleteAllHeldSeatsByUser(userId);

        // delete redis ticket of user
        redisTicketRepository.deleteById(userId);

        return ticketRepository.save(ticket);
    }

    public Ticket getById(Integer id) {
        log.info("Get ticket by id");

        return ticketRepository.findById(id).orElseThrow(() -> new AppException(ErrorApp.TICKET_NOT_FOUND));
    }

    public List<Ticket> getAllTickets(Integer userId, Integer showTimeId) {
        log.info("Get all tickets or by userId={}, showTimeId={}", userId, showTimeId);

        // get ticket by user
        if (userId != null && showTimeId == null) {
            log.info("Fetching tickets by userId={}", userId);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new AppException(ErrorApp.USER_NOT_FOUND));
            return ticketRepository.findAllByUser(user);
        }

        // get ticket by show time id
        if (showTimeId != null && userId != null) {
            log.info("Fetching tickets by showTimeId={}", showTimeId);
            ShowTime showTime = showTimeRepository.findById(showTimeId)
                    .orElseThrow(() -> new AppException(ErrorApp.SHOW_TIME_NOT_FOUND));
            return ticketRepository.findAllByShowTime(showTime);
        }

        log.info("Fetching all tickets");
        return ticketRepository.findAll();
    }

}
