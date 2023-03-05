package com.jp.pssior.assignment.constant;

import lombok.Getter;

@Getter
public enum ErrorCode {

    MAX_SEAT_ROW_SET("ERR001", "Exceed maximum {0} seat per row set"),
    MAX_ROW_PER_SHOW("ERR002", "Exceed maximum {0} show per row set"),
    SHOW_NO_NOT_FOUND("ERR003", "Show No {0} not exist"),
    BOOKED_SEAT_FOUND("ERR004", "Seat No {0} had been booked by other. Please select again"),
    BOOKED_PHONE_NO_EXIST("ERR005", "Only one booking per phone# is allowed per show"),
    SEAT_NO_NOT_EXIST("ERR006", "Seat No not found"),
    NOT_SEAT_AVAILABLE("ERR0007", "Not seat available for this show no. {0}"),
    TICKET_NO_NOT_FOUND("ERR008", "Ticket no {0} not found"),
    PHONE_NO_UNMATCH_TICKET("ERR009", "Ticket no {0} not match with phone no {1} booked"),
    CANCELLATION_NOT_ALLOWED("ERR010", "Cancellation after {0} is not allowed"),
    COMMAND_NOT_FOUND("ERR011", "Command {0} not found"),
    ;

    private String code;
    private String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
