package ru.bfad.bfaApp.bots.dto;

import lombok.Data;

@Data
public class BelongFromAgent {
    private String fio;
    private String phone;
    private SmallAgentDto agent;
}
