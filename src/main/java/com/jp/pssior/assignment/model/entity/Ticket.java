package com.jp.pssior.assignment.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@Table(name = "TICKET")
@AllArgsConstructor
@NoArgsConstructor
public class Ticket implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "TICKET_NO")
    private String ticketNo;

    @ManyToOne
    @JoinColumn(name = "BOOK_NO", nullable = false)
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "SEAT_ID", nullable = false)
    private ShowSeat showSeat;

    @Column(name = "CANCELLED_DT")
    private LocalDateTime cancelledTime;

    @Version
    private int version;
}
