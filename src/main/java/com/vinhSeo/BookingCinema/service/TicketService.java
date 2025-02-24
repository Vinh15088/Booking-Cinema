package com.vinhSeo.BookingCinema.service;

import com.vinhSeo.BookingCinema.dto.request.TicketDetailRequest;
import com.vinhSeo.BookingCinema.dto.request.TicketRequest;
import com.vinhSeo.BookingCinema.exception.AppException;
import com.vinhSeo.BookingCinema.exception.ErrorApp;
import com.vinhSeo.BookingCinema.mapper.TicketDetailMapper;
import com.vinhSeo.BookingCinema.mapper.TicketMapper;
import com.vinhSeo.BookingCinema.model.ShowTime;
import com.vinhSeo.BookingCinema.model.Ticket;
import com.vinhSeo.BookingCinema.model.User;
import com.vinhSeo.BookingCinema.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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

    public Ticket createTicket(TicketRequest ticketRequest) {
        log.info("Create ticket");

        List<TicketDetailRequest> showTimeSeatList = ticketRequest.getTicketDetailRequests();

        for(TicketDetailRequest request:showTimeSeatList) {
            showTimeSeatService.changeStatus(request.getShowTimeSeatId(), "RESERVED");
        }

        Ticket ticket = ticketMapper.toTicket(ticketRequest, userRepository, showTimeRepository,
                ticketDetailMapper, showTimeSeatRepository, ticketPriceRepository);

        ticketDetailRepository.saveAll(ticket.getTicketDetails());

        ticket.setBookingDate(new Date());

        if (ticket.getTicketDetails() != null) {
            ticket.getTicketDetails().forEach(detail -> detail.setTicket(ticket));
        }

        return ticketRepository.save(ticket);
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

    public Ticket updateTicket(Integer id, TicketRequest ticketRequest) {
        log.info("Update ticket by id");

        Ticket ticket = getById(id);

        Ticket newTicket = ticketMapper.toTicket(ticketRequest, userRepository, showTimeRepository,
                ticketDetailMapper, showTimeSeatRepository, ticketPriceRepository);
        newTicket.setId(id);

        return ticketRepository.save(newTicket);
    }

    public void deleteTicket(Integer id) {
        log.info("Delete ticket by id");

        Ticket ticket = getById(id);

        ticketRepository.delete(ticket);
    }
}
