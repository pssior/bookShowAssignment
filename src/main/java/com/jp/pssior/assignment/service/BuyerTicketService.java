package com.jp.pssior.assignment.service;

import com.jp.pssior.assignment.constant.ErrorCode;
import com.jp.pssior.assignment.model.dto.BookShow;
import com.jp.pssior.assignment.model.entity.Booking;
import com.jp.pssior.assignment.model.entity.ShowSeat;
import com.jp.pssior.assignment.exception.ShowException;
import com.jp.pssior.assignment.model.entity.ShowSetup;
import com.jp.pssior.assignment.model.entity.Ticket;
import com.jp.pssior.assignment.repo.BookingRepository;
import com.jp.pssior.assignment.repo.ShowSeatRepository;
import com.jp.pssior.assignment.repo.ShowSetupRepository;
import com.jp.pssior.assignment.repo.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BuyerTicketService {

    private final ShowSeatRepository showSeatRepository;
    private final ShowSetupRepository showSetupRepository;
    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;

    public String getAvailableSeatByShowNo(String showNo) throws ShowException {

        List<ShowSeat> showSeats = showSeatRepository.findByShowSetup_showNo(showNo);
        if (CollectionUtils.isEmpty(showSeats)) {
            throw new ShowException(ErrorCode.NOT_SEAT_AVAILABLE, showNo);
        }
        String availableSeatsNo = showSeats.stream()
                .map(ShowSeat::getSeatNo)
                .sorted()
                .collect(Collectors.joining(", "));

        log.info("Available seat: {}", availableSeatsNo);
        return availableSeatsNo;
    }

    public List<Ticket> reserveShow(BookShow bookShow) throws ShowException {

        Optional<ShowSetup> showSetupOpt = showSetupRepository.findById(bookShow.getShowNo());
        if (showSetupOpt.isEmpty()) {
            throw new ShowException(ErrorCode.SHOW_NO_NOT_FOUND, bookShow.getShowNo());
        }
        ShowSetup showSetup = showSetupOpt.get();

        //check phoneNo per show or not
        List<Booking> phoneBooking = bookingRepository.findByPhoneNoAndShowNo(bookShow.getPhoneNo(), bookShow.getShowNo());
        if (!CollectionUtils.isEmpty(phoneBooking)) {
            throw new ShowException(ErrorCode.BOOKED_PHONE_NO_EXIST);
        }

        List<Ticket> tickets = ticketRepository.findByBooking_ShowSetup_showNoAndCancelledTimeIsNull(bookShow.getShowNo());
        if (!CollectionUtils.isEmpty(tickets)) {
            //check seat available or not
            Set<String> seatsTaken = tickets.stream()
                    .map(ticket -> ticket.getShowSeat().getSeatNo())
                    .collect(Collectors.toSet());
            if (!CollectionUtils.isEmpty(seatsTaken)) {
                seatsTaken.retainAll(bookShow.getSeats());
                if (seatsTaken.size() > 0) {
                    throw new ShowException(ErrorCode.BOOKED_SEAT_FOUND);
                }
            }
        }

        List<ShowSeat> showSeats = showSeatRepository.findByShowSetup_showNo(bookShow.getShowNo());
        if (!CollectionUtils.isEmpty(showSeats)) {
            Set<String> availableSeat = showSeats.stream().map(ShowSeat::getSeatNo).collect(Collectors.toSet());
            if (bookShow.getSeats().stream().anyMatch(a -> !availableSeat.contains(a))) {
                throw new ShowException(ErrorCode.SEAT_NO_NOT_EXIST);
            }
            showSeats = showSeats.stream()
                    .filter(a -> bookShow.getSeats().contains(a.getSeatNo()))
                    .collect(Collectors.toList());

        }

        DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
        String ticketNoPrefix = bookShow.getShowNo() + dtFormat.format(LocalDateTime.now());
        Booking booking = new Booking(bookShow, showSetup);
        booking.setBookedNo(UUID.randomUUID());
        booking.setBookedDate(LocalDateTime.now());

        List<Ticket> bookTickets = showSeats.stream()
                .map(seat -> {
                    Ticket ticket = new Ticket();
                    ticket.setShowSeat(seat);
                    ticket.setBooking(booking);
                    ticket.setTicketNo(ticketNoPrefix + seat.getSeatNo());
                    return ticket;
                }).collect(Collectors.toList());
        booking.setTicketList(new ArrayList<>(bookTickets));

        bookingRepository.save(booking);
        log.info("Ticket booked successful - phone# {} ", bookShow.getPhoneNo());
        tickets.forEach(ticket -> {
            log.info("Ticket No.: {}  - Seat No: {}", ticket.getTicketNo(), ticket.getShowSeat().getSeatNo());
        });

        return bookTickets;
    }

    public Ticket cancelTicket(String phoneNo, String ticketNo) throws ShowException {
        log.info("Cancel {} ticket - start", ticketNo);
        Optional<Ticket> ticketOpt = ticketRepository.findFirstByTicketNo(ticketNo);
        if (ticketOpt.isEmpty()) {
            throw new ShowException(ErrorCode.TICKET_NO_NOT_FOUND, ticketNo);
        }
        Ticket ticket = ticketOpt.get();
        if (!phoneNo.equals(ticket.getBooking().getPhoneNo())) {
            throw new ShowException(ErrorCode.PHONE_NO_UNMATCH_TICKET, ticketNo, phoneNo);
        }
        Booking booking = ticket.getBooking();
        LocalDateTime currentDt = LocalDateTime.now();
        Duration duration = Duration.between(booking.getBookedDate(), currentDt);
        Integer cancelMinute = booking.getShowSetup().getCancelWindowMinutes();
        Integer minDifferent = Math.toIntExact(duration.toMinutes());
        if (minDifferent > cancelMinute) {
            log.error("Ticket unable to cancel where {} min after {} min book allowed ", minDifferent, cancelMinute);
            throw new ShowException(ErrorCode.CANCELLATION_NOT_ALLOWED, cancelMinute.toString());
        }

        ticket.setCancelledTime(LocalDateTime.now());
        ticketRepository.save(ticket);
        log.info("Cancel {} ticket - successful", ticketNo);
        return ticket;
    }
}
