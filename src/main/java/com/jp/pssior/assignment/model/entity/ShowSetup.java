package com.jp.pssior.assignment.model.entity;


import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@Table(name = "SHOW_SETUP")
@Builder
@AllArgsConstructor
public class ShowSetup implements Serializable {

    @Id
    @Column(name = "SHOW_NO", nullable = false)
    private String showNo;

    @Column(name = "ROW_PER_SHOW", nullable = false)
    private Integer rowPerShow;

    @Column(name = "ROW_PER_SEAT", nullable = false)
    private Integer rowPerSeat;

    @Column(name = "CNCL_WD_MIN", nullable = false)
    private Integer cancelWindowMinutes;

    @Column(name = "CREATED_DT")
    private LocalDateTime createdDate;

    @Version
    private Integer version;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "showSetup", cascade = CascadeType.PERSIST)
    private Set<ShowSeat> showSeats;

    public ShowSetup(String showNo, String rowPerShow, String rowPerSeat, String cancelWindowMinutes) {
        this.showNo = showNo;
        this.rowPerShow = Integer.valueOf(rowPerShow);
        this.rowPerSeat = Integer.valueOf(rowPerSeat);
        this.cancelWindowMinutes = Integer.valueOf(cancelWindowMinutes);
    }
}
