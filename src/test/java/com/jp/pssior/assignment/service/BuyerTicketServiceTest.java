package com.jp.pssior.assignment.service;

import com.jp.pssior.assignment.constant.ErrorCode;
import com.jp.pssior.assignment.exception.ShowException;
import com.jp.pssior.assignment.model.dto.BookShow;
import com.jp.pssior.assignment.model.entity.Booking;
import com.jp.pssior.assignment.model.entity.ShowSeat;
import com.jp.pssior.assignment.model.entity.ShowSetup;
import com.jp.pssior.assignment.model.entity.Ticket;
import com.jp.pssior.assignment.repo.BookingRepository;
import com.jp.pssior.assignment.repo.ShowSeatRepository;
import com.jp.pssior.assignment.repo.ShowSetupRepository;
import com.jp.pssior.assignment.repo.TicketRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class BuyerTicketServiceTest {

    @Mock
    private ShowSeatRepository showSeatRepository;
    @Mock
    private ShowSetupRepository showSetupRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private TicketRepository ticketRepository;

    private BuyerTicketService buyerTicketService;
    private List<ShowSeat> mockShowSeat;
    @Captor
    private ArgumentCaptor<Booking> bookingArgumentCaptor;

    @BeforeEach
    void setup() {
        buyerTicketService = new BuyerTicketService(showSeatRepository, showSetupRepository,
                bookingRepository, ticketRepository);
        ShowSeat showSeat = ShowSeat.builder()
                .seatNo("A1")
                .id(1)
                .build();
        ShowSeat showSeat2 = ShowSeat.builder()
                .seatNo("A2")
                .id(2)
                .build();
        mockShowSeat = Arrays.asList(showSeat, showSeat2);
    }


    @Test
    @SneakyThrows
    void getAvailableSeatByShowNo() {
        String showNo = "1";
        when(showSeatRepository.findByShowSetup_showNo(showNo)).thenReturn(mockShowSeat);
        String availableSeatWithComma = buyerTicketService.getAvailableSeatByShowNo(showNo);
        assertEquals("A1, A2", availableSeatWithComma);
    }

    @Test
    void getAvailableSeatNotFound() {
        String showNo = "1";
        when(showSeatRepository.findByShowSetup_showNo(showNo)).thenReturn(new ArrayList<>());
        ShowException showException = assertThrows(ShowException.class, () -> buyerTicketService.getAvailableSeatByShowNo(showNo));
        assertEquals(ErrorCode.NOT_SEAT_AVAILABLE, showException.getErrorCode());
    }

    @Test
    @SneakyThrows
    void cancelTicketSuccess() {
        String phoneNo = "1234";
        String ticketNo = "00001";
        Ticket ticket = mockTicket(ticketNo, phoneNo);
        when(ticketRepository.findFirstByTicketNo(ticket.getTicketNo())).thenReturn(Optional.of(ticket));
        Ticket result = buyerTicketService.cancelTicket(phoneNo, ticketNo);
        assertNotNull(result.getCancelledTime());
    }

    @Test
    @SneakyThrows
    void cancelTicketIfNotExist() {
        String phoneNo = "1234";
        String ticketNo = "00001";
        when(ticketRepository.findFirstByTicketNo(ticketNo)).thenReturn(Optional.empty());
        ShowException exception = assertThrows(ShowException.class, () -> buyerTicketService.cancelTicket(phoneNo, ticketNo));
        assertEquals(ErrorCode.TICKET_NO_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @SneakyThrows
    void cancelTicketIfPhoneNotMatch() {
        String ticketNo = "00001";
        Ticket ticket = mockTicket(ticketNo, "2233");
        when(ticketRepository.findFirstByTicketNo(ticket.getTicketNo())).thenReturn(Optional.of(ticket));
        ShowException exception = assertThrows(ShowException.class, () -> buyerTicketService.cancelTicket("1234", ticketNo));
        assertEquals(ErrorCode.PHONE_NO_UNMATCH_TICKET, exception.getErrorCode());
    }

    @Test
    void cancelTicketWindowMinuteExpiry() {
        String phoneNo = "1234";
        String ticketNo = "00001";
        Ticket ticket = mockTicket(ticketNo, phoneNo);
        LocalDateTime passTime = LocalDateTime.now().minusMinutes(4);
        ticket.getBooking().setBookedDate(passTime);
        when(ticketRepository.findFirstByTicketNo(ticket.getTicketNo())).thenReturn(Optional.of(ticket));
        ShowException exception = assertThrows(ShowException.class, () -> buyerTicketService.cancelTicket(phoneNo, ticketNo));
        assertEquals(ErrorCode.CANCELLATION_NOT_ALLOWED, exception.getErrorCode());
    }

    @Test
    void reserveShowByWrongShowNo() {
        String showNo = "B01";
        BookShow bookShow = new BookShow();
        bookShow.setSeats(new HashSet<>(Arrays.asList("A1", "A2")));
        bookShow.setPhoneNo("1234");
        bookShow.setShowNo(showNo);
        when(showSetupRepository.findById(showNo)).thenReturn(Optional.empty());
        ShowException exception = assertThrows(ShowException.class, () -> buyerTicketService.reserveShow(bookShow));
        assertEquals(ErrorCode.SHOW_NO_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void reserveShowBySamePhoneNoMade() {
        String showNo = "B01";
        ShowSetup showSetup = mockShowSetup(showNo);
        BookShow bookShow = new BookShow();
        bookShow.setSeats(new HashSet<>(Arrays.asList("A1", "A2")));
        bookShow.setPhoneNo("1234");
        bookShow.setShowNo(showNo);
        when(showSetupRepository.findById(showNo)).thenReturn(Optional.of(showSetup));
        when(bookingRepository.findByPhoneNoAndShowNo(bookShow.getPhoneNo(), bookShow.getShowNo()))
                .thenReturn(Arrays.asList(Booking.builder()
                        .phoneNo(bookShow.getPhoneNo())
                        .build()));
        ShowException exception = assertThrows(ShowException.class, () -> buyerTicketService.reserveShow(bookShow));
        assertEquals(ErrorCode.BOOKED_PHONE_NO_EXIST, exception.getErrorCode());
    }

    @Test
    void reserveShowBySeatNoNotAvailability() {
        String showNo = "B01";
        ShowSetup showSetup = mockShowSetup(showNo);
        BookShow bookShow = new BookShow();
        bookShow.setSeats(new HashSet<>(Arrays.asList("A1", "A2")));
        bookShow.setPhoneNo("1234");
        bookShow.setShowNo(showNo);

        when(showSetupRepository.findById(showNo)).thenReturn(Optional.of(showSetup));
        when(bookingRepository.findByPhoneNoAndShowNo(bookShow.getPhoneNo(), bookShow.getShowNo()))
                .thenReturn(Collections.emptyList());

        Ticket mockTicket = mockTicket("123", bookShow.getPhoneNo());
        mockTicket.setShowSeat(ShowSeat.builder()
                .seatNo("A1")
                .build());
        when(ticketRepository.findByBooking_ShowSetup_showNoAndCancelledTimeIsNull(showNo))
                .thenReturn(Arrays.asList(mockTicket));
        ShowException showException = assertThrows(ShowException.class, () -> buyerTicketService.reserveShow(bookShow));
        assertEquals(ErrorCode.BOOKED_SEAT_FOUND, showException.getErrorCode());
    }

    @Test
    void reserveTicketByWrongSeatNo() {
        String showNo = "B01";
        ShowSetup showSetup = mockShowSetup(showNo);
        BookShow bookShow = new BookShow();
        bookShow.setSeats(new HashSet<>(Arrays.asList("B1", "A1")));
        bookShow.setPhoneNo("1234");
        bookShow.setShowNo(showNo);

        when(showSetupRepository.findById(showNo)).thenReturn(Optional.of(showSetup));
        when(bookingRepository.findByPhoneNoAndShowNo(bookShow.getPhoneNo(), bookShow.getShowNo()))
                .thenReturn(Collections.emptyList());
        when(ticketRepository.findByBooking_ShowSetup_showNoAndCancelledTimeIsNull(showNo))
                .thenReturn(Collections.emptyList());

        List<ShowSeat> showSeats = Arrays.asList(ShowSeat.builder()
                .seatNo("A1")
                .build(), ShowSeat.builder()
                .seatNo("A2")
                .build());
        when(showSeatRepository.findByShowSetup_showNo(showNo)).thenReturn(showSeats);
        ShowException showException = assertThrows(ShowException.class, () -> buyerTicketService.reserveShow(bookShow));
        assertEquals(ErrorCode.SEAT_NO_NOT_EXIST, showException.getErrorCode());
    }

    @Test
    @SneakyThrows
    void reserveTicketSuccessful() {

        String showNo = "B01";
        ShowSetup showSetup = mockShowSetup(showNo);
        BookShow bookShow = new BookShow();
        bookShow.setSeats(new HashSet<>(Arrays.asList("A2", "A1")));
        bookShow.setPhoneNo("1234");
        bookShow.setShowNo(showNo);

        when(showSetupRepository.findById(showNo)).thenReturn(Optional.of(showSetup));
        when(bookingRepository.findByPhoneNoAndShowNo(bookShow.getPhoneNo(), bookShow.getShowNo()))
                .thenReturn(Collections.emptyList());
        when(ticketRepository.findByBooking_ShowSetup_showNoAndCancelledTimeIsNull(showNo))
                .thenReturn(Collections.emptyList());

        List<ShowSeat> showSeats = Arrays.asList(ShowSeat.builder()
                .seatNo("A1")
                .build(), ShowSeat.builder()
                .seatNo("A2")
                .build());
        when(showSeatRepository.findByShowSetup_showNo(showNo)).thenReturn(showSeats);
        buyerTicketService.reserveShow(bookShow);
        verify(bookingRepository, times(1)).save(bookingArgumentCaptor.capture());
        Booking result = bookingArgumentCaptor.getValue();
        assertEquals(2, result.getTicketList().size());
        assertNotNull(result.getBookedDate());
    }

    private ShowSetup mockShowSetup(String showNo) {
        return ShowSetup.builder()
                .showNo(showNo)
                .cancelWindowMinutes(2)
                .rowPerSeat(10)
                .rowPerShow(1)
                .build();
    }

    private Ticket mockTicket(String ticketNo, String phoneNo) {
        return Ticket.builder()
                .ticketNo(ticketNo)
                .version(1)
                .booking(Booking.builder()
                        .phoneNo(phoneNo)
                        .bookedNo(UUID.randomUUID())
                        .bookedDate(LocalDateTime.now())
                        .version(1)
                        .showSetup(ShowSetup.builder()
                                .showNo("1")
                                .cancelWindowMinutes(2)
                                .build())
                        .build())
                .build();
    }
}
