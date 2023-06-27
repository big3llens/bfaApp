package ru.bfad.bfaApp.webComponent.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import ru.bfad.bfaApp.webComponent.models.From;

import java.util.List;


@Data
@AllArgsConstructor
public class ListsInit {
    private List<MailslistDto> mailslist;
    private List<From> listFrom;
}
