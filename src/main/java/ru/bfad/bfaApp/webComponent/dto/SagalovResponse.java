package ru.bfad.bfaApp.webComponent.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SagalovResponse {
    private String status;
    private int code;
    private String timestamp;
    private String resulte;
}
