package ru.bfad.bfaApp.webComponent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailsRequest {
    private List<PersonRequest> persons;
    private String emails;
}
