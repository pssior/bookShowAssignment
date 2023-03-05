package com.jp.pssior.assignment.repo;

import com.jp.pssior.assignment.model.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    List<Ticket> findByBooking_ShowSetup_showNoAndCancelledTimeIsNull(String showNo);

    Optional<Ticket> findFirstByTicketNo(String ticketNo);
}
