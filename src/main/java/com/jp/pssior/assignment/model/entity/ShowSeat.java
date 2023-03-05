package com.jp.pssior.assignment.model.entity;


import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "SHOW_SEAT")
public class ShowSeat implements Serializable {

    @Id
    @Column(name = "SEAT_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "SHOW_NO", nullable = false)
    private ShowSetup showSetup;

    @Column(name = "SEAT_NO")
    private String seatNo;

    @Version
    private int version;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "showSeat")
    List<Ticket> ticketList;
}
