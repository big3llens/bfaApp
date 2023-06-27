package ru.bfad.bfaApp.bots.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AgentDto {
    private Long userid;
    private Long chatid;
    private String fio;
    private String phone;
    private String agency;
    private String fullname;

    public AgentDto(Long userid, Long chatid) {
        this.userid = userid;
        this.chatid = chatid;
    }
}
