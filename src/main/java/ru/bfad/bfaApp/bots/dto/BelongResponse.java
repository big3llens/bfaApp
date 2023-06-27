package ru.bfad.bfaApp.bots.dto;

import lombok.Data;

@Data
public class BelongResponse {
    private Long userid;
    private Long chatid;
    private String status;
    private String fio;
    private String phone;
    private String validtill;
}
