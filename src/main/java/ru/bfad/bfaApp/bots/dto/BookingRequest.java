package ru.bfad.bfaApp.bots.dto;

import lombok.Data;

@Data
public class BookingRequest {
    private String passportdetails;
    private String client_guid;
    private AgentDto agent;
}
