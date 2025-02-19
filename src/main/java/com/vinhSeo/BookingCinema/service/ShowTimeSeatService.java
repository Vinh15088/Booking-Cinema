package com.vinhSeo.BookingCinema.service;

import com.vinhSeo.BookingCinema.enums.ShowTimeSeatStatus;
import com.vinhSeo.BookingCinema.exception.AppException;
import com.vinhSeo.BookingCinema.exception.ErrorApp;
import com.vinhSeo.BookingCinema.model.ShowTime;
import com.vinhSeo.BookingCinema.model.ShowTimeSeat;
import com.vinhSeo.BookingCinema.repository.ShowTimeRepository;
import com.vinhSeo.BookingCinema.repository.ShowTimeSeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j(topic = "SHOW_TIME_SEAT_SERVICE")
@RequiredArgsConstructor
public class ShowTimeSeatService {

    private final ShowTimeSeatRepository showTimeSeatRepository;
    private final ShowTimeRepository showTimeRepository;

    public ShowTimeSeat getById(Integer id) {
        return showTimeSeatRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorApp.SHOW_TIME_SEAT_NOT_FOUND));
    }

    public List<ShowTimeSeat> getAllByShowTime(Integer showTimeId) {
        ShowTime showTime = showTimeRepository.findById(showTimeId).orElseThrow(() ->
                new AppException(ErrorApp.SHOW_TIME_NOT_FOUND));

        return showTimeSeatRepository.findAllByShowTime(showTime);
    }

    public ShowTimeSeat changeStatus(Integer id, String status) {
        ShowTimeSeat showTimeSeat = getById(id);

        status.toUpperCase();

        if(!status.equals("AVAILABLE") && !status.equals("PENDING") && !status.equals("RESERVED")) {
            throw new AppException(ErrorApp.SHOW_TIME_SEAT_STATUS_NOT_CORRECT);
        }

        if(status.equals("AVAILABLE")) showTimeSeat.setShowTimeSeatStatus(ShowTimeSeatStatus.AVAILABLE);
        if(status.equals("PENDING")) showTimeSeat.setShowTimeSeatStatus(ShowTimeSeatStatus.PENDING);
        if(status.equals("RESERVED")) showTimeSeat.setShowTimeSeatStatus(ShowTimeSeatStatus.RESERVED);

        return showTimeSeatRepository.save(showTimeSeat);
    }
}
