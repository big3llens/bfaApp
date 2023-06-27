package ru.bfad.bfaApp.bots.dto;

import lombok.Data;

@Data
public class BookingFromAgent {
//    private String passportdetails;
    private String client_guid;
    private String  objectid;
//    private String booking_before;
    private SmallAgentDto agent;
}
