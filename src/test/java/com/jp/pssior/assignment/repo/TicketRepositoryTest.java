package com.jp.pssior.assignment.repo;

import com.jp.pssior.assignment.model.entity.Ticket;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TicketRepositoryTest extends RepositoryTest {

    @Autowired
    private TicketRepository ticketRepository;

    @Test
    @Sql("/availableSeat.sql")
    void findByShowNo(){
        List<Ticket> tickets = ticketRepository.findByBooking_ShowSetup_showNoAndCancelledTimeIsNull("1");
        Optional<Ticket> ticketOpt = ticketRepository.findFirstByTicketNo("123456");
        assertEquals(1, tickets.size());
        assertFalse(ticketOpt.isEmpty());
    }

}
