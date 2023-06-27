package ru.bfad.bfaApp.bots.dto;

import lombok.Data;

@Data
public class RegistrationResponse {
    private Long userid;
    private Long chatid;
    private byte status;
}
