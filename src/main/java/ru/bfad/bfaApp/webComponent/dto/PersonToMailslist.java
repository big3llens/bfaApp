package ru.bfad.bfaApp.webComponent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PersonToMailslist {
    private Integer mailslistId;
    private String email;
    private String fullName;
}
