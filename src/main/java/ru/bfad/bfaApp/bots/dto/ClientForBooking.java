package ru.bfad.bfaApp.bots.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientForBooking {
    private String fio;
    private String phone;
    private String guid;
}
