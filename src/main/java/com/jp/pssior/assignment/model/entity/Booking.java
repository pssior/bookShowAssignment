package com.jp.pssior.assignment.model.entity;

import com.jp.pssior.assignment.model.dto.BookShow;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "BOOKING")
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class Booking implements Serializable {

    @Id
    @Column(name = "BOOK_NO")
    private UUID bookedNo;

    @Column(name = "PHONE_NO")
    private String phoneNo;

    @ManyToOne
    @JoinColumn(name = "SHOW_NO")
    private ShowSetup showSetup;

    @Column(name = "BOOKED_DT")
    private LocalDateTime bookedDate;

    @Version
    private int version;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<Ticket> ticketList;

    public Booking(BookShow bookShow, ShowSetup showSetup) {
        this.phoneNo = bookShow.getPhoneNo();
        this.showSetup = showSetup;
    }
}
