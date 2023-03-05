package com.jp.pssior.assignment.service;

import com.jp.pssior.assignment.constant.ErrorCode;
import com.jp.pssior.assignment.model.dto.SeatAllocate;
import com.jp.pssior.assignment.model.entity.Booking;
import com.jp.pssior.assignment.model.entity.ShowSeat;
import com.jp.pssior.assignment.model.entity.ShowSetup;
import com.jp.pssior.assignment.exception.ShowException;
import com.jp.pssior.assignment.model.entity.Ticket;
import com.jp.pssior.assignment.repo.ShowSetupRepository;
import com.jp.pssior.assignment.repo.TicketRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class AdminSetupShowServiceTest {

    @Mock
    private ShowSetupRepository showSetupRepository;
    @Mock
    private TicketRepository ticketRepository;
    private AdminSetupShowService adminSetupShowService;
    private ShowSetup mockShowSetup;

    @BeforeEach
    void setup() {
        mockShowSetup = new ShowSetup("1111", "26", "10", "2");
        adminSetupShowService = new AdminSetupShowService(showSetupRepository, ticketRepository);
        ReflectionTestUtils.setField(adminSetupShowService, "maxSeatPerRow", 10);
        ReflectionTestUtils.setField(adminSetupShowService, "maxShowPerRow", 26);
    }

    @Test
    @SneakyThrows
    @DisplayName("Throw exception when max show per row hit")
    void maxShowPerRowHit() {
        mockShowSetup.setRowPerShow(30);
        ShowException exception = assertThrows(ShowException.class, () -> adminSetupShowService.setupShow(mockShowSetup));
        assertEquals(ErrorCode.MAX_ROW_PER_SHOW, exception.getErrorCode());
    }

    @Test
    @SneakyThrows
    @DisplayName("Throw exception when max seat per row hit")
    void maxSeatPerRowHit() {
        mockShowSetup.setRowPerSeat(12);
        ShowException exception = assertThrows(ShowException.class, () -> adminSetupShowService.setupShow(mockShowSetup));
        log.info("error msg {}", exception.getMessage());
        assertEquals(ErrorCode.MAX_SEAT_ROW_SET, exception.getErrorCode());
    }

    @Test
    @SneakyThrows
    @DisplayName("Show setup successful")
    void showSetupSuccess() {
        ShowSetup showSetupResult = adminSetupShowService.setupShow(mockShowSetup);
        assertNotNull(showSetupResult.getCreatedDate());
        assertNotNull(showSetupResult.getShowSeats());
        assertEquals(mockShowSetup.getCancelWindowMinutes(), showSetupResult.getCancelWindowMinutes());
    }

    @Test
    @SneakyThrows
    void viewShow() {
        String showNo = "S1";
        List<Ticket> mockTicketList = Arrays.asList(Ticket.builder()
                .ticketNo("1234")
                .booking(Booking.builder()
                        .phoneNo("999999")
                        .build())
                .showSeat(ShowSeat.builder()
                        .seatNo("A1")
                        .build())
                .build());
        when(ticketRepository.findByBooking_ShowSetup_showNoAndCancelledTimeIsNull(showNo)).thenReturn(mockTicketList);
        List<SeatAllocate> seatAllocates = adminSetupShowService.viewShow(showNo);
        assertFalse(CollectionUtils.isEmpty(seatAllocates));
        assertEquals("1234", seatAllocates.get(0).getTicketNo());
    }

    @Test
    @SneakyThrows
    void viewShowNotSeatAllocateFound() {
        String showNo = "S1";
        when(ticketRepository.findByBooking_ShowSetup_showNoAndCancelledTimeIsNull(showNo)).thenReturn(Collections.EMPTY_LIST);
        List<SeatAllocate> seatAllocates = adminSetupShowService.viewShow(showNo);
        assertTrue(CollectionUtils.isEmpty(seatAllocates));
    }
}
