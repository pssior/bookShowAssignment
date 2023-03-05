package com.jp.pssior.assignment.service;

import com.jp.pssior.assignment.constant.ErrorCode;
import com.jp.pssior.assignment.constant.ShowConstant;
import com.jp.pssior.assignment.model.dto.SeatAllocate;
import com.jp.pssior.assignment.model.entity.ShowSeat;
import com.jp.pssior.assignment.model.entity.ShowSetup;
import com.jp.pssior.assignment.exception.ShowException;
import com.jp.pssior.assignment.model.entity.Ticket;
import com.jp.pssior.assignment.repo.ShowSetupRepository;
import com.jp.pssior.assignment.repo.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminSetupShowService {

    private final ShowSetupRepository showSetupRepository;
    private final TicketRepository ticketRepository;
    @Value("${max.seat.per.row:10}")
    private Integer maxSeatPerRow;
    @Value("${max.show.per.row:26}")
    private Integer maxShowPerRow;

    public ShowSetup setupShow(ShowSetup showSetup) throws ShowException {
        log.info("Setup show {} - start", showSetup.getShowNo());

        if (maxShowPerRow.compareTo(showSetup.getRowPerShow()) == -1) {
            throw new ShowException(ErrorCode.MAX_ROW_PER_SHOW, maxShowPerRow.toString());
        }
        if (maxSeatPerRow.compareTo(showSetup.getRowPerSeat()) == -1) {
            throw new ShowException(ErrorCode.MAX_SEAT_ROW_SET, maxSeatPerRow.toString());
        }

        showSetup.setCreatedDate(LocalDateTime.now());
        showSetup.setShowSeats(new HashSet<>());
        for (int showRow = 0; showRow < showSetup.getRowPerShow(); showRow++) {
            String seatPrefix = String.valueOf(ShowConstant.ALPHABETS[showRow]);
            for (int seatRow = 1; seatRow <= showSetup.getRowPerSeat(); seatRow++) {
                String seatNo = seatPrefix + seatRow;
                showSetup.getShowSeats().add(ShowSeat.builder()
                        .showSetup(showSetup)
                        .seatNo(seatNo)
                        .build());
            }
        }

        showSetupRepository.save(showSetup);
        log.info("Setup show {} - successful", showSetup.getShowNo());
        return showSetup;
    }

    public List<SeatAllocate> viewShow(String showNo) {
        log.info("View show no {} seat allocation start", showNo);
        List<Ticket> tickets = ticketRepository.findByBooking_ShowSetup_showNoAndCancelledTimeIsNull(showNo);
        if (!CollectionUtils.isEmpty(tickets)) {
            List<SeatAllocate> seatAllocates = tickets.stream()
                    .map(ticket -> SeatAllocate.builder()
                            .buyerPhone(ticket.getBooking().getPhoneNo())
                            .showNo(showNo)
                            .ticketNo(ticket.getTicketNo())
                            .seatNo(ticket.getShowSeat().getSeatNo())
                            .build()).collect(Collectors.toList());
            log.info(" Show No. , Ticket , Buyer Phone , Seat No ");
            seatAllocates.stream().forEach(seatAllocate -> log.info(" {} , {} , {} , {}",
                    seatAllocate.getShowNo(), seatAllocate.getTicketNo(), seatAllocate.getBuyerPhone(), seatAllocate.getSeatNo()));
            return seatAllocates;
        }
        log.warn("View show - {} not seat allocation found ", showNo);
        return Collections.EMPTY_LIST;
    }


}
