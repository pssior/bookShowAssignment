package com.jp.pssior.assignment.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeatAllocate implements Serializable {
    private String showNo;
    private String ticketNo;
    private String buyerPhone;
    private String seatNo;
}
